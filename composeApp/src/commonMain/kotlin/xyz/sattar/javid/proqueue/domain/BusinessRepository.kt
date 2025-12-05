package xyz.sattar.javid.proqueue.domain

import xyz.sattar.javid.proqueue.domain.model.Business

interface BusinessRepository {
    suspend fun upsertBusiness(business: Business): Boolean
    suspend fun loadAllBusiness(): List<Business>
    suspend fun getBusinessById(businessId: Long): Business?
    suspend fun deleteBusiness(businessId: Long): Boolean
}