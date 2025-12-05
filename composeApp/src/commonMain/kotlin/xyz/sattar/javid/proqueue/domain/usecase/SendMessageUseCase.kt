package xyz.sattar.javid.proqueue.domain.usecase

import xyz.sattar.javid.proqueue.domain.MessageRepository
import xyz.sattar.javid.proqueue.domain.model.Message

class SendMessageUseCase(private val repository: MessageRepository) {
    suspend operator fun invoke(appointmentId: Long, type: String) {
        val message = Message(
            id = 0, // Auto-generated
            appointmentId = appointmentId,
            messageType = type,
            content = "Message of type $type sent", // Placeholder content
            sentAt = 0L
        )
        repository.insertMessage(message)
    }
}
