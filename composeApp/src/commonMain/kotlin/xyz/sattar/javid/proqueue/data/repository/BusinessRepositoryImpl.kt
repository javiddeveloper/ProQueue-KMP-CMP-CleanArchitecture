package xyz.sattar.javid.proqueue.data.repository

import xyz.sattar.javid.proqueue.data.localDataSource.dao.BusinessDao
import xyz.sattar.javid.proqueue.data.localDataSource.mapper.toDomain
import xyz.sattar.javid.proqueue.data.localDataSource.mapper.toEntity
import xyz.sattar.javid.proqueue.domain.BusinessRepository
import xyz.sattar.javid.proqueue.domain.model.Business
import xyz.sattar.javid.proqueue.domain.model.Visitor

class BusinessRepositoryImpl(
    private val dao: BusinessDao
) : BusinessRepository {
    override suspend fun upsertVisitor(visitor: Visitor): Boolean {
        return try {
            dao.upsertVisitor(visitor.toEntity())
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun upsertBusiness(business: Business): Boolean {
        return try {
            dao.upsertBusiness(business.toEntity())
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun loadLastBusiness(): Business? {
        return try {
            dao.loadLastBusiness().toDomain()
        } catch (e: Exception) {
            null
        }
    }


}