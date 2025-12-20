package xyz.sattar.javid.proqueue.feature.createAppointment

sealed interface CreateAppointmentIntent {
    data class LoadAppointment(val appointmentId: Long) : CreateAppointmentIntent
    data class SelectVisitor(val visitorId: Long) : CreateAppointmentIntent
    data class CreateAppointment(
        val visitorId: Long,
        val appointmentDate: Long,
        val serviceDuration: Int?,
        val description: String?,
        val force: Boolean = false
    ) : CreateAppointmentIntent
    data object BackPress : CreateAppointmentIntent
    data object AppointmentCreated : CreateAppointmentIntent
    data object DismissConflictDialog : CreateAppointmentIntent
}
