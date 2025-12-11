package xyz.sattar.javid.proqueue.feature.createAppointment

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import xyz.sattar.javid.proqueue.core.state.BusinessStateHolder
import xyz.sattar.javid.proqueue.core.ui.BaseViewModel
import xyz.sattar.javid.proqueue.domain.usecase.CreateAppointmentUseCase
import xyz.sattar.javid.proqueue.domain.usecase.GetAppointmentByIdUseCase
import xyz.sattar.javid.proqueue.domain.usecase.GetVisitorByIdUseCase
import xyz.sattar.javid.proqueue.domain.usecase.UpdateAppointmentUseCase

class CreateAppointmentViewModel(
    private val getVisitorByIdUseCase: GetVisitorByIdUseCase,
    private val createAppointmentUseCase: CreateAppointmentUseCase,
    private val getAppointmentByIdUseCase: GetAppointmentByIdUseCase,
    private val updateAppointmentUseCase: UpdateAppointmentUseCase
) : BaseViewModel<CreateAppointmentState, CreateAppointmentState.PartialState, CreateAppointmentEvent, CreateAppointmentIntent>(
    initialState = CreateAppointmentState()
) {
    override fun handleIntent(intent: CreateAppointmentIntent): Flow<CreateAppointmentState.PartialState> {
        return when (intent) {
            is CreateAppointmentIntent.LoadAppointment -> loadAppointment(intent.appointmentId)
            is CreateAppointmentIntent.SelectVisitor -> flow {
                emit(CreateAppointmentState.PartialState.IsLoading(true))
                try {
                    val visitor = getVisitorByIdUseCase(intent.visitorId)
                    visitor?.let {
                        emit(CreateAppointmentState.PartialState.LoadVisitor(visitor))
                    }
                } catch (e: Exception) {
                    emit(
                        CreateAppointmentState.PartialState.ShowMessage(
                            e.message ?: "خطا در بارگذاری لیست مراجعین"
                        )
                    )
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

            is CreateAppointmentState.PartialState.LoadVisitor ->
                currentState.copy(
                    visitor = partialState.visitor,
                    selectedVisitorId = partialState.visitor.id,
                    serviceDuration = currentState.serviceDuration
                        ?: BusinessStateHolder.selectedBusiness.value?.defaultServiceDuration,
                    isLoading = false
                )

            is CreateAppointmentState.PartialState.LoadAppointmentDetails ->
                currentState.copy(
                    selectedVisitorId = partialState.visitorId,
                    appointmentDate = partialState.appointmentDate,
                    serviceDuration = partialState.serviceDuration,
                    editingAppointmentId = partialState.appointmentId,
                    isLoading = false
                )

            CreateAppointmentState.PartialState.AppointmentCreated ->
                currentState.copy(
                    appointmentCreated = true,
                    isLoading = false
                )
        }
    }

    override fun createErrorState(message: String): CreateAppointmentState.PartialState =
        CreateAppointmentState.PartialState.ShowMessage(message)


    private fun loadAppointment(appointmentId: Long): Flow<CreateAppointmentState.PartialState> =
        flow {
            emit(CreateAppointmentState.PartialState.IsLoading(true))
            try {
                val appointment = getAppointmentByIdUseCase(appointmentId)
                if (appointment != null) {
                    emit(
                        CreateAppointmentState.PartialState.LoadAppointmentDetails(
                            visitorId = appointment.visitorId,
                            appointmentDate = appointment.appointmentDate,
                            serviceDuration = appointment.serviceDuration,
                            appointmentId = appointment.id
                        )
                    )
                } else {
                    emit(CreateAppointmentState.PartialState.ShowMessage("نوبت یافت نشد"))
                }
            } catch (e: Exception) {
                emit(
                    CreateAppointmentState.PartialState.ShowMessage(
                        e.message ?: "خطا در بارگذاری نوبت"
                    )
                )
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

            val editingId = uiState.value.editingAppointmentId
            val success = if (editingId != null) {
                updateAppointmentUseCase(
                    appointmentId = editingId,
                    date = appointmentDate,
                    duration = serviceDuration
                )
            } else {
                createAppointmentUseCase(
                    businessId = business.id,
                    visitorId = visitorId,
                    appointmentDate = appointmentDate,
                    serviceDuration = serviceDuration
                )
            }
            if (success) {
                emit(CreateAppointmentState.PartialState.AppointmentCreated)
            } else {
                emit(CreateAppointmentState.PartialState.ShowMessage("خطا در ذخیره نوبت"))
            }
        } catch (e: Exception) {
            emit(CreateAppointmentState.PartialState.ShowMessage(e.message ?: "خطا در عملیات"))
        }
    }
}
