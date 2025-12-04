package xyz.sattar.javid.proqueue.domain

import xyz.sattar.javid.proqueue.domain.model.Business
import xyz.sattar.javid.proqueue.domain.model.Visitor

interface BusinessRepository {

    suspend fun upsertVisitor(visitor: Visitor): Boolean
    suspend fun upsertBusiness(business: Business): Boolean

    suspend fun loadAllBusiness(): List<Business>
    suspend fun deleteBusiness(businessId: Int)
}