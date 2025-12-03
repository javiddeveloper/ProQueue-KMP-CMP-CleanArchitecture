package xyz.sattar.javid.proqueue.data.repository

import xyz.sattar.javid.proqueue.data.localDataSource.dao.BusinessDao
import xyz.sattar.javid.proqueue.data.localDataSource.mapper.toEntity
import xyz.sattar.javid.proqueue.domain.VisitorRepository
import xyz.sattar.javid.proqueue.domain.model.Visitor

class VisitorRepositoryImpl(
    private val businessDao: BusinessDao
) : VisitorRepository {
    override suspend fun upsertVisitor(visitor: Visitor): Boolean {
        return try {
            businessDao.upsertVisitor(visitor.toEntity())
            true
        } catch (e: Exception) {
            false
        }
    }
}