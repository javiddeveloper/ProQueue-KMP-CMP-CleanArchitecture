package xyz.sattar.javid.proqueue.domain.usecase

import xyz.sattar.javid.proqueue.domain.BusinessRepository
import xyz.sattar.javid.proqueue.domain.model.Business

class LoadAllBusinessUseCase(private val repository: BusinessRepository) {
    suspend operator fun invoke(): List<Business> =
        repository.loadAllBusiness()
}