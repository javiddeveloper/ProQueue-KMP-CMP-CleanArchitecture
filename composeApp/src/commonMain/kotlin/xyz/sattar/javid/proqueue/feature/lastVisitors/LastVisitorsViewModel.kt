package xyz.sattar.javid.proqueue.feature.lastVisitors

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import xyz.sattar.javid.proqueue.core.state.BusinessStateHolder
import xyz.sattar.javid.proqueue.core.ui.BaseViewModel
import xyz.sattar.javid.proqueue.core.utils.DateTimeUtils
import xyz.sattar.javid.proqueue.domain.usecase.GetTodayAppointmentsUseCase
import xyz.sattar.javid.proqueue.domain.usecase.RemoveAppointmentUseCase
import kotlin.time.ExperimentalTime

class LastVisitorsViewModel(
    private val getTodayAppointmentsUseCase: GetTodayAppointmentsUseCase,
    private val removeAppointmentUseCase: RemoveAppointmentUseCase
) : BaseViewModel<LastVisitorsState, LastVisitorsState.PartialState, LastVisitorsEvent, LastVisitorsIntent>(
    initialState = LastVisitorsState()
) {

    override fun handleIntent(intent: LastVisitorsIntent): Flow<LastVisitorsState.PartialState> {
        return when (intent) {
            LastVisitorsIntent.LoadAppointments -> loadAppointments()
            is LastVisitorsIntent.OnAppointmentOptionsClick -> flow {
                emit(LastVisitorsState.PartialState.ShowOptionsDialog(intent.appointmentId))
            }
            LastVisitorsIntent.OnCreateAppointmentClick -> sendEvent(LastVisitorsEvent.NavigateToCreateAppointment)
            is LastVisitorsIntent.OnEditAppointment -> {
                sendEvent(LastVisitorsEvent.NavigateToEditAppointment(intent.appointmentId))
            }
            is LastVisitorsIntent.OnDeleteAppointment -> deleteAppointment(intent.appointmentId)
            LastVisitorsIntent.DismissDialog -> flow {
                emit(LastVisitorsState.PartialState.ShowOptionsDialog(null))
            }
        }
    }

    override fun reduceState(
        currentState: LastVisitorsState,
        partialState: LastVisitorsState.PartialState
    ): LastVisitorsState {
        return when (partialState) {
            is LastVisitorsState.PartialState.IsLoading ->
                currentState.copy(isLoading = partialState.isLoading)
            is LastVisitorsState.PartialState.ShowMessage ->
                currentState.copy(message = partialState.message, isLoading = false)
            is LastVisitorsState.PartialState.LoadAppointments ->
                currentState.copy(
                    appointments = partialState.appointments,
                    totalCount = partialState.totalCount,
                    isLoading = false
                )
            is LastVisitorsState.PartialState.ShowOptionsDialog ->
                currentState.copy(
                    selectedAppointmentId = partialState.appointmentId,
                    showOptionsDialog = partialState.appointmentId != null
                )
        }
    }

    override fun createErrorState(message: String): LastVisitorsState.PartialState =
        LastVisitorsState.PartialState.ShowMessage(message)

    @OptIn(ExperimentalTime::class)
    private fun loadAppointments(): Flow<LastVisitorsState.PartialState> = flow {
        emit(LastVisitorsState.PartialState.IsLoading(true))
        try {
            val business = BusinessStateHolder.selectedBusiness.value
            if (business != null) {
                val today = DateTimeUtils.systemCurrentMilliseconds()
                val appointments = getTodayAppointmentsUseCase(business.id, today)
                emit(
                    LastVisitorsState.PartialState.LoadAppointments(
                        appointments = appointments,
                        totalCount = appointments.size
                    )
                )
            } else {
                emit(LastVisitorsState.PartialState.ShowMessage("لطفاً ابتدا یک کسب‌وکار انتخاب کنید"))
            }
        } catch (e: Exception) {
            emit(LastVisitorsState.PartialState.ShowMessage(e.message ?: "خطا در بارگذاری نوبت‌ها"))
        }
    }

    private fun deleteAppointment(appointmentId: Long): Flow<LastVisitorsState.PartialState> = flow {
        try {
            val success = removeAppointmentUseCase(appointmentId)
            if (success) {
                emit(LastVisitorsState.PartialState.ShowOptionsDialog(null))
                // Reload appointments
                val business = BusinessStateHolder.selectedBusiness.value
                if (business != null) {
                    @OptIn(ExperimentalTime::class)
                    val today = DateTimeUtils.systemCurrentMilliseconds()
                    val appointments = getTodayAppointmentsUseCase(business.id, today)
                    emit(
                        LastVisitorsState.PartialState.LoadAppointments(
                            appointments = appointments,
                            totalCount = appointments.size
                        )
                    )
                }
            } else {
                emit(LastVisitorsState.PartialState.ShowMessage("خطا در حذف نوبت"))
            }
        } catch (e: Exception) {
            emit(LastVisitorsState.PartialState.ShowMessage(e.message ?: "خطا در حذف نوبت"))
        }
    }
}

