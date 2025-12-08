package xyz.sattar.javid.proqueue.domain.usecase

import xyz.sattar.javid.proqueue.domain.AppointmentRepository
import xyz.sattar.javid.proqueue.domain.VisitorRepository

class DeleteVisitorUseCase(
    private val visitorRepository: VisitorRepository,
    private val appointmentRepository: AppointmentRepository
) {
    suspend operator fun invoke(visitorId: Long) {
        appointmentRepository.deleteAppointmentsByVisitorId(visitorId)
        visitorRepository.deleteVisitor(visitorId)
    }
}
