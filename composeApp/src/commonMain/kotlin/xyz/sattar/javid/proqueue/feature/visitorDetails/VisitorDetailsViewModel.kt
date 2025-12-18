package xyz.sattar.javid.proqueue.feature.visitorDetails

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import xyz.sattar.javid.proqueue.core.state.BusinessStateHolder
import xyz.sattar.javid.proqueue.core.ui.BaseViewModel
import xyz.sattar.javid.proqueue.domain.AppointmentRepository
import xyz.sattar.javid.proqueue.domain.MessageRepository
import xyz.sattar.javid.proqueue.domain.VisitorRepository
import xyz.sattar.javid.proqueue.feature.visitorDetails.VisitorDetailsState.PartialState

class VisitorDetailsViewModel(
    private val visitorRepository: VisitorRepository,
    private val appointmentRepository: AppointmentRepository,
    private val messageRepository: MessageRepository
) : BaseViewModel<VisitorDetailsState, PartialState, VisitorDetailsEvent, VisitorDetailsIntent>(
    initialState = VisitorDetailsState()
) {

    override fun handleIntent(intent: VisitorDetailsIntent): Flow<PartialState> {
        return when (intent) {
            is VisitorDetailsIntent.LoadVisitorDetails -> loadVisitorDetails(intent.visitorId)
            VisitorDetailsIntent.BackPress -> {
                sendEvent(VisitorDetailsEvent.NavigateBack)
            }
        }
    }

    override fun reduceState(
        currentState: VisitorDetailsState,
        partialState: PartialState
    ): VisitorDetailsState {
        return when (partialState) {
            is PartialState.IsLoading -> currentState.copy(isLoading = partialState.isLoading)
            is PartialState.ShowMessage -> currentState.copy(message = partialState.message, isLoading = false)
            is PartialState.DetailsLoaded -> currentState.copy(
                visitor = partialState.visitor,
                appointments = partialState.appointments,
                messages = partialState.messages,
                isLoading = false
            )
        }
    }

    override fun createErrorState(message: String): PartialState = PartialState.ShowMessage(message)

    private fun loadVisitorDetails(visitorId: Long): Flow<PartialState> = flow {
        emit(PartialState.IsLoading(true))
        try {
            val visitor = visitorRepository.getVisitorById(visitorId)
            val business = BusinessStateHolder.selectedBusiness.value

            if (visitor != null && business != null) {
                val appointments = appointmentRepository.getVisitorHistoryForBusiness(visitorId, business.id)
                val messages = messageRepository.getMessagesForVisitorAndBusiness(visitorId, business.id)
                emit(PartialState.DetailsLoaded(visitor, appointments, messages))
            } else {
                emit(PartialState.ShowMessage("اطلاعات یافت نشد"))
            }
        } catch (e: Exception) {
            emit(PartialState.ShowMessage(e.message ?: "خطا در بارگذاری اطلاعات"))
        }
    }
}
