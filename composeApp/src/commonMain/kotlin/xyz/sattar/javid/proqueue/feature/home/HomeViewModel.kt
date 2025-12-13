package xyz.sattar.javid.proqueue.feature.home

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import xyz.sattar.javid.proqueue.core.state.BusinessStateHolder
import xyz.sattar.javid.proqueue.core.ui.BaseViewModel
import xyz.sattar.javid.proqueue.domain.model.AppointmentWithDetails
import xyz.sattar.javid.proqueue.domain.usecase.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


class HomeViewModel(
    private val getWaitingQueueUseCase: GetWaitingQueueUseCase,
    private val getTodayStatsUseCase: GetTodayStatsUseCase,
    private val removeAppointmentUseCase: RemoveAppointmentUseCase,
    private val markAppointmentCompletedUseCase: MarkAppointmentCompletedUseCase,
    private val markAppointmentNoShowUseCase: MarkAppointmentNoShowUseCase,
    private val sendMessageUseCase: SendMessageUseCase
) : BaseViewModel<HomeState, HomeState.PartialState, HomeEvent, HomeIntent>(
    initialState = HomeState()
) {
    override fun handleIntent(intent: HomeIntent): Flow<HomeState.PartialState> {
        return when (intent) {
            HomeIntent.LoadData -> loadData()
            is HomeIntent.RemoveAppointment -> removeAppointment(intent.appointmentId)
            is HomeIntent.MarkAppointmentCompleted -> markCompleted(intent.appointmentId)
            is HomeIntent.MarkAppointmentNoShow -> markNoShow(intent.appointmentId)
            is HomeIntent.SendMessage -> sendMessage(intent.appointmentId, intent.type)
        }
    }

    override fun reduceState(
        currentState: HomeState,
        partialState: HomeState.PartialState
    ): HomeState {
        return when (partialState) {
            is HomeState.PartialState.IsLoading ->
                currentState.copy(isLoading = partialState.isLoading)
            is HomeState.PartialState.ShowMessage ->
                currentState.copy(message = partialState.message, isLoading = false)
            is HomeState.PartialState.LoadBusinessName ->
                currentState.copy(business = partialState.business, isLoading = false)
            is HomeState.PartialState.LoadQueue ->
                currentState.copy(queue = partialState.queue, isLoading = false)
            is HomeState.PartialState.LoadStats ->
                currentState.copy(stats = partialState.stats, isLoading = false)
        }
    }

    override fun createErrorState(message: String): HomeState.PartialState =
        HomeState.PartialState.ShowMessage(message)

    private fun loadData(): Flow<HomeState.PartialState> = flow {
        emit(HomeState.PartialState.IsLoading(true))
        val business = BusinessStateHolder.selectedBusiness.value
        emit(HomeState.PartialState.LoadBusinessName(business))

        if (business != null) {
            try {
                // Load Queue
                @OptIn(ExperimentalTime::class)
                val today = Clock.System.now().toEpochMilliseconds()
                val queue = getWaitingQueueUseCase(business.id, today)
                val queueItems = calculateQueueTimes(queue)
                emit(HomeState.PartialState.LoadQueue(queueItems))

                // Load Stats
                val stats = getTodayStatsUseCase(business.id)
                 emit(HomeState.PartialState.LoadStats(stats))
            } catch (e: Exception) {
                emit(HomeState.PartialState.ShowMessage(e.message ?: "Error loading data"))
            }
        }
    }

    private fun calculateQueueTimes(appointments: List<AppointmentWithDetails>): List<QueueItem> {
        var currentTime = 0L
        // If first appointment is in the future, start from there.
        // Otherwise start from now.
        
        return appointments.map { item ->
            val appointment = item.appointment
            val visitor = item.visitor
            
            val startTime = if (appointment.appointmentDate > currentTime) {
                appointment.appointmentDate
            } else {
                currentTime
            }
            val duration = (appointment.serviceDuration ?: 15) * 60 * 1000L // default 15 mins
            val endTime = startTime + duration
            
            currentTime = endTime // Next appointment starts when this one ends
            
            QueueItem(
                appointment = appointment,
                visitorName = visitor.fullName,
                visitorPhone = visitor.phoneNumber,
                estimatedStartTime = startTime,
                estimatedEndTime = endTime
            )
        }
    }

    private fun removeAppointment(appointmentId: Long): Flow<HomeState.PartialState> = flow {
        removeAppointmentUseCase(appointmentId)
        emitAll(loadData()) // Reload to update queue
    }

    private fun markCompleted(appointmentId: Long): Flow<HomeState.PartialState> = flow {
        markAppointmentCompletedUseCase(appointmentId)
        emitAll(loadData())
    }

    private fun markNoShow(appointmentId: Long): Flow<HomeState.PartialState> = flow {
        markAppointmentNoShowUseCase(appointmentId)
        emitAll(loadData())
    }

    private fun sendMessage(appointmentId: Long, type: String): Flow<HomeState.PartialState> = flow {
        sendMessageUseCase(appointmentId, type)
        emitAll(loadData())
    }
}
