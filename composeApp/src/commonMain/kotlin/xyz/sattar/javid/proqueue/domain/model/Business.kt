package xyz.sattar.javid.proqueue.domain.model

data class Business(
    val id: Long,
    val title: String,
    val phone: String,
    val address: String,
    val logoPath: String,
    val defaultServiceDuration: Int,
    val workStartHour: Int,
    val workEndHour: Int,
    val notificationEnabled: Boolean,
    val notificationTypes: String,
    val notificationMinutesBefore: Int,
    val createdAt: Long,
)

