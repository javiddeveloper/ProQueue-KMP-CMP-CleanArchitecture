package xyz.sattar.javid.proqueue.feature.createAppointment

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import xyz.sattar.javid.proqueue.core.state.BusinessStateHolder
import xyz.sattar.javid.proqueue.core.ui.BaseViewModel
import xyz.sattar.javid.proqueue.domain.usecase.CreateAppointmentUseCase
import xyz.sattar.javid.proqueue.domain.usecase.GetAllVisitorsUseCase

class CreateAppointmentViewModel(
    private val getAllVisitorsUseCase: GetAllVisitorsUseCase,
    private val createAppointmentUseCase: CreateAppointmentUseCase
) : BaseViewModel<CreateAppointmentState, CreateAppointmentState.PartialState, CreateAppointmentEvent, CreateAppointmentIntent>(
    initialState = CreateAppointmentState()
) {
    override fun handleIntent(intent: CreateAppointmentIntent): Flow<CreateAppointmentState.PartialState> {
        return when (intent) {
            CreateAppointmentIntent.LoadVisitors -> loadVisitors()
            is CreateAppointmentIntent.SelectVisitor -> flow {
                // We don't need to do anything here as the UI handles the selection state
                // But if we wanted to pre-select in VM, we could emit a state change
                // For now, let's just reload visitors to ensure we have the latest list including the new one
                emit(CreateAppointmentState.PartialState.IsLoading(true))
                try {
                    val visitors = getAllVisitorsUseCase()
                    val visitorOptions = visitors.map {
                        VisitorOption(
                            id = it.id,
                            fullName = it.fullName,
                            phoneNumber = it.phoneNumber
                        )
                    }
                    emit(CreateAppointmentState.PartialState.LoadVisitors(visitorOptions))
                } catch (e: Exception) {
                    emit(CreateAppointmentState.PartialState.ShowMessage(e.message ?: "خطا در بارگذاری لیست مراجعین"))
                }
            }
            is CreateAppointmentIntent.CreateAppointment -> createAppointment(
                intent.visitorId,
                intent.appointmentDate,
                intent.serviceDuration
            )
            CreateAppointmentIntent.BackPress -> sendEvent(CreateAppointmentEvent.NavigateBack)
        }
    }

    override fun reduceState(
        currentState: CreateAppointmentState,
        partialState: CreateAppointmentState.PartialState
    ): CreateAppointmentState {
        return when (partialState) {
            is CreateAppointmentState.PartialState.IsLoading ->
                currentState.copy(isLoading = partialState.isLoading)
            is CreateAppointmentState.PartialState.ShowMessage ->
                currentState.copy(message = partialState.message, isLoading = false)
            is CreateAppointmentState.PartialState.LoadVisitors ->
                currentState.copy(visitors = partialState.visitors, isLoading = false)
            CreateAppointmentState.PartialState.AppointmentCreated ->
                currentState.copy(isLoading = false)
        }
    }

    override fun createErrorState(message: String): CreateAppointmentState.PartialState =
        CreateAppointmentState.PartialState.ShowMessage(message)

    private fun loadVisitors(): Flow<CreateAppointmentState.PartialState> = flow {
        emit(CreateAppointmentState.PartialState.IsLoading(true))
        try {
            val visitors = getAllVisitorsUseCase()
            val visitorOptions = visitors.map {
                VisitorOption(
                    id = it.id,
                    fullName = it.fullName,
                    phoneNumber = it.phoneNumber
                )
            }
            emit(CreateAppointmentState.PartialState.LoadVisitors(visitorOptions))
        } catch (e: Exception) {
            emit(CreateAppointmentState.PartialState.ShowMessage(e.message ?: "خطا در بارگذاری لیست مراجعین"))
        }
    }

    private fun createAppointment(
        visitorId: Long,
        appointmentDate: Long,
        serviceDuration: Int?
    ): Flow<CreateAppointmentState.PartialState> = flow {
        emit(CreateAppointmentState.PartialState.IsLoading(true))
        try {
            val business = BusinessStateHolder.selectedBusiness.value
            if (business == null) {
                emit(CreateAppointmentState.PartialState.ShowMessage("لطفاً ابتدا یک کسب‌وکار انتخاب کنید"))
                return@flow
            }

            val success = createAppointmentUseCase(
                businessId = business.id,
                visitorId = visitorId,
                appointmentDate = appointmentDate,
                serviceDuration = serviceDuration
            )

            if (success) {
                emit(CreateAppointmentState.PartialState.AppointmentCreated)
                sendEvent(CreateAppointmentEvent.AppointmentCreated)
            } else {
                emit(CreateAppointmentState.PartialState.ShowMessage("این مراجع قبلاً برای امروز نوبت دارد"))
            }
        } catch (e: Exception) {
            emit(CreateAppointmentState.PartialState.ShowMessage(e.message ?: "خطا در ایجاد نوبت"))
        }
    }
}
