package xyz.sattar.javid.proqueue.domain.model

data class Message(
    val id: Long,
    val appointmentId: Long,
    val messageType: String,
    val content: String,
    val sentAt: Long,
) {
}