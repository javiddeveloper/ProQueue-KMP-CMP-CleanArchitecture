package xyz.sattar.javid.proqueue.data.repository

import xyz.sattar.javid.proqueue.data.localDataSource.dao.BusinessDao
import xyz.sattar.javid.proqueue.domain.VisitorRepository

class VisitorRepositoryImpl(
    private val dao: BusinessDao
): VisitorRepository {
    override fun upsertVisitor(): Boolean {
       return true
    }

    override fun upsertBusiness(): Boolean {
        return true
    }

}