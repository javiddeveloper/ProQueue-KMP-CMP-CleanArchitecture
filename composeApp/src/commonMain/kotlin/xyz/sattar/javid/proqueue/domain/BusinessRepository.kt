package xyz.sattar.javid.proqueue.domain

import xyz.sattar.javid.proqueue.domain.model.Business
import xyz.sattar.javid.proqueue.domain.model.Visitor

interface BusinessRepository {

    fun upsertVisitor(visitor: Visitor): Boolean

    fun upsertBusiness(business: Business): Boolean
}