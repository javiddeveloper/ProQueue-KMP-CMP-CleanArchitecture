package xyz.sattar.javid.proqueue.data.repository

import xyz.sattar.javid.proqueue.data.localDataSource.dao.BusinessDao
import xyz.sattar.javid.proqueue.domain.BusinessRepository
import xyz.sattar.javid.proqueue.domain.model.Business
import xyz.sattar.javid.proqueue.domain.model.Visitor

class BusinessRepositoryImpl(
    private val dao: BusinessDao
): BusinessRepository {
    override fun upsertVisitor(visitor: Visitor): Boolean {
        TODO("Not yet implemented")
    }

    override fun upsertBusiness(business: Business): Boolean {
        TODO("Not yet implemented")
    }


}