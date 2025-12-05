package xyz.sattar.javid.proqueue.domain.usecase

import xyz.sattar.javid.proqueue.domain.VisitorRepository
import xyz.sattar.javid.proqueue.domain.model.Visitor

class GetAllVisitorsUseCase(private val repository: VisitorRepository) {
    suspend operator fun invoke(): List<Visitor> = repository.getAllVisitors()
}
