package xyz.sattar.javid.proqueue.domain.model

data class Appointment(
    val id: Long,
    val businessId: Long,
    val visitorId: Long,
    val appointmentDate: Long,
    val serviceDuration: Int?,
    val status: String,
    val queuePosition: Int,
    val createdAt: Long,
    val updatedAt: Long,
)