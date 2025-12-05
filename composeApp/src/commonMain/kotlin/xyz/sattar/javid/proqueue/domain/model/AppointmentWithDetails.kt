package xyz.sattar.javid.proqueue.domain.model

data class AppointmentWithDetails(
    val appointment: Appointment,
    val visitor: Visitor,
    val business: Business
)