package xyz.sattar.javid.proqueue.domain.usecase

import xyz.sattar.javid.proqueue.domain.BusinessRepository
import xyz.sattar.javid.proqueue.domain.model.Business

class LoadBusinessUseCase(private val repository: BusinessRepository) {
    suspend operator fun invoke(): Business? =
        repository.loadLastBusiness()
}