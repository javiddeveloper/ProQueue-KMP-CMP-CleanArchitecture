package xyz.sattar.javid.proqueue.feature.lastVisitors

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import xyz.sattar.javid.proqueue.core.state.BusinessStateHolder
import xyz.sattar.javid.proqueue.core.ui.BaseViewModel
import xyz.sattar.javid.proqueue.core.utils.DateTimeUtils
import xyz.sattar.javid.proqueue.domain.usecase.GetTodayAppointmentsUseCase
import xyz.sattar.javid.proqueue.domain.usecase.RemoveAppointmentUseCase
import xyz.sattar.javid.proqueue.domain.usecase.MarkAppointmentCompletedUseCase
import xyz.sattar.javid.proqueue.domain.usecase.MarkAppointmentNoShowUseCase
import xyz.sattar.javid.proqueue.domain.usecase.SendMessageUseCase
import kotlin.time.ExperimentalTime

class LastVisitorsViewModel(
    private val getTodayAppointmentsUseCase: GetTodayAppointmentsUseCase,
    private val removeAppointmentUseCase: RemoveAppointmentUseCase,
    private val markAppointmentCompletedUseCase: MarkAppointmentCompletedUseCase,
    private val markAppointmentNoShowUseCase: MarkAppointmentNoShowUseCase,
    private val sendMessageUseCase: SendMessageUseCase
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
            is LastVisitorsIntent.OnMarkCompleted -> markCompleted(intent.appointmentId)
            is LastVisitorsIntent.OnMarkNoShow -> markNoShow(intent.appointmentId)
            LastVisitorsIntent.DismissDialog -> flow {
                emit(LastVisitorsState.PartialState.ShowOptionsDialog(null))
            }
            is LastVisitorsIntent.OnTabSelected -> flow {
                emit(LastVisitorsState.PartialState.TabSelected(intent.index))
            }
            is LastVisitorsIntent.OnAppointmentClick -> {
                sendEvent(LastVisitorsEvent.NavigateToVisitorDetails(intent.visitorId))
            }
            is LastVisitorsIntent.OnSendMessage -> flow {
                try {
                    val success = sendMessageUseCase(
                        appointmentId = intent.appointmentId,
                        type = intent.type,
                        content = intent.content,
                        businessTitle = intent.businessTitle
                    )
                    if (!success) emit(LastVisitorsState.PartialState.ShowMessage("خطا در ثبت پیام"))
                } catch (e: Exception) {
                    emit(LastVisitorsState.PartialState.ShowMessage(e.message ?: "خطا در ثبت پیام"))
                }
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
            is LastVisitorsState.PartialState.TabSelected ->
                currentState.copy(selectedTab = partialState.index)
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

    private fun markCompleted(appointmentId: Long): Flow<LastVisitorsState.PartialState> = flow {
        try {
            val success = markAppointmentCompletedUseCase(appointmentId)
            if (success) {
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
                }
            } else {
                emit(LastVisitorsState.PartialState.ShowMessage("خطا در تکمیل نوبت"))
            }
        } catch (e: Exception) {
            emit(LastVisitorsState.PartialState.ShowMessage(e.message ?: "خطا در تکمیل نوبت"))
        }
    }

    private fun markNoShow(appointmentId: Long): Flow<LastVisitorsState.PartialState> = flow {
        try {
            val success = markAppointmentNoShowUseCase(appointmentId)
            if (success) {
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
                }
            } else {
                emit(LastVisitorsState.PartialState.ShowMessage("خطا در ثبت عدم مراجعه"))
            }
        } catch (e: Exception) {
            emit(LastVisitorsState.PartialState.ShowMessage(e.message ?: "خطا در ثبت عدم مراجعه"))
        }
    }
}
