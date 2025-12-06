package xyz.sattar.javid.proqueue.data.repository

import xyz.sattar.javid.proqueue.data.localDataSource.dao.VisitorDao
import xyz.sattar.javid.proqueue.data.localDataSource.mapper.toDomain
import xyz.sattar.javid.proqueue.data.localDataSource.mapper.toEntity
import xyz.sattar.javid.proqueue.domain.VisitorRepository
import xyz.sattar.javid.proqueue.domain.model.Visitor

class VisitorRepositoryImpl(
    private val visitorDao: VisitorDao
) : VisitorRepository {
    override suspend fun upsertVisitor(visitor: Visitor): Long {
        return try {
            visitorDao.upsertVisitor(visitor.toEntity())
        } catch (e: Exception) {
            -1L
        }
    }

    override suspend fun getVisitorByPhone(phone: String): Visitor? {
        return try {
            visitorDao.getVisitorByPhone(phone)?.toDomain()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getVisitorById(visitorId: Long): Visitor? {
        return try {
            visitorDao.getVisitorById(visitorId)?.toDomain()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getAllVisitors(): List<Visitor> {
        return try {
            visitorDao.getAllVisitors().map { it.toDomain() }
        } catch (e: Exception) {
            emptyList()
        }
    }
}