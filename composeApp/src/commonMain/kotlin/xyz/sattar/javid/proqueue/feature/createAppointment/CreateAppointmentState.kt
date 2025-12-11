package xyz.sattar.javid.proqueue.feature.createAppointment

import androidx.compose.runtime.Immutable
import xyz.sattar.javid.proqueue.domain.model.Visitor

@Immutable
data class CreateAppointmentState(
    val isLoading: Boolean = false,
    val message: String? = null,
    val visitor: Visitor? = null,
    val selectedVisitorId: Long? = null,
    val appointmentDate: Long = 0L,
    val serviceDuration: Int? = null,
    val editingAppointmentId: Long? = null
) {
    sealed class PartialState {
        data class IsLoading(val isLoading: Boolean) : PartialState()
        data class ShowMessage(val message: String) : PartialState()
        data class LoadVisitor(val visitor: Visitor) : PartialState()
        data class LoadAppointmentDetails(
            val visitorId: Long,
            val appointmentDate: Long,
            val serviceDuration: Int?,
            val appointmentId: Long
        ) : PartialState()
        data object AppointmentCreated : PartialState()
    }
}

