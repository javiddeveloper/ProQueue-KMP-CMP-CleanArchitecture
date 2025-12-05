package xyz.sattar.javid.proqueue.feature.createAppointment

import androidx.compose.runtime.Immutable

@Immutable
data class CreateAppointmentState(
    val isLoading: Boolean = false,
    val message: String? = null,
    val visitors: List<VisitorOption> = emptyList(),
    val selectedVisitorId: Long? = null,
    val appointmentDate: Long = 0L,
    val serviceDuration: Int? = null
) {
    sealed class PartialState {
        data class IsLoading(val isLoading: Boolean) : PartialState()
        data class ShowMessage(val message: String) : PartialState()
        data class LoadVisitors(val visitors: List<VisitorOption>) : PartialState()
        data object AppointmentCreated : PartialState()
    }
}

data class VisitorOption(
    val id: Long,
    val fullName: String,
    val phoneNumber: String
)
