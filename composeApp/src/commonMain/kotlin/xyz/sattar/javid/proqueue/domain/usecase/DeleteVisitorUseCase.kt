package xyz.sattar.javid.proqueue.domain.usecase

import xyz.sattar.javid.proqueue.domain.AppointmentRepository
import xyz.sattar.javid.proqueue.domain.MessageRepository
import xyz.sattar.javid.proqueue.domain.VisitorRepository

class DeleteVisitorUseCase(
    private val visitorRepository: VisitorRepository,
    private val appointmentRepository: AppointmentRepository,
    private val messageRepository: MessageRepository
) {
    suspend operator fun invoke(visitorId: Long) {
        try {
            messageRepository.deleteMessagesByVisitorId(visitorId)
        } catch (_: Exception) {}
        appointmentRepository.deleteAppointmentsByVisitorId(visitorId)
        visitorRepository.deleteVisitor(visitorId)
    }
}
