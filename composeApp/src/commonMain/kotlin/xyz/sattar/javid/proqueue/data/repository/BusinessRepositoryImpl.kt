package xyz.sattar.javid.proqueue.data.repository

import xyz.sattar.javid.proqueue.data.localDataSource.dao.BusinessDao
import xyz.sattar.javid.proqueue.data.localDataSource.mapper.toDomain
import xyz.sattar.javid.proqueue.data.localDataSource.mapper.toEntity
import xyz.sattar.javid.proqueue.domain.BusinessRepository
import xyz.sattar.javid.proqueue.domain.model.Business

class BusinessRepositoryImpl(
    private val businessDao: BusinessDao
) : BusinessRepository {
    override suspend fun upsertBusiness(business: Business): Boolean {
        return try {
            businessDao.upsertBusiness(business.toEntity())
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun loadAllBusiness(): List<Business> {
        return try {
            businessDao.loadAllBusiness().map { it.toDomain() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getBusinessById(businessId: Long): Business? {
        return try {
            businessDao.getBusinessById(businessId)?.toDomain()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun deleteBusiness(businessId: Long): Boolean {
        return try {
            businessDao.deleteBusiness(businessId)
            true
        } catch (e: Exception) {
            false
        }
    }
}