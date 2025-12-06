package xyz.sattar.javid.proqueue.domain.usecase

import xyz.sattar.javid.proqueue.domain.VisitorRepository
import xyz.sattar.javid.proqueue.domain.model.Visitor

class VisitorUpsertUseCase(
    private val visitorRepository: VisitorRepository
) {
    suspend operator fun invoke(visitor: Visitor): Long {
        return visitorRepository.upsertVisitor(visitor)
    }
}
