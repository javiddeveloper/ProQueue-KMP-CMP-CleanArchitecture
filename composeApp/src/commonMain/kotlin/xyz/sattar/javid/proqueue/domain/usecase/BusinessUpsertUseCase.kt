package xyz.sattar.javid.proqueue.domain.usecase

import xyz.sattar.javid.proqueue.domain.BusinessRepository
import xyz.sattar.javid.proqueue.domain.model.Business
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


class BusinessUpsertUseCase(private val repository: BusinessRepository) {
    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(business: Business): Boolean =
        repository.upsertBusiness(business.copy(
            createTimeStamp = Clock.System.now().toEpochMilliseconds())
        )
}