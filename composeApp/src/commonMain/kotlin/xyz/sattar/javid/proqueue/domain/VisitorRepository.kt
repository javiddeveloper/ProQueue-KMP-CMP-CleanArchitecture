package xyz.sattar.javid.proqueue.domain

import xyz.sattar.javid.proqueue.domain.model.Visitor

interface VisitorRepository {
    suspend fun upsertVisitor(visitor: Visitor): Boolean
    suspend fun getVisitorByPhone(phone: String): Visitor?
    suspend fun getVisitorById(visitorId: Long): Visitor?
    suspend fun getAllVisitors(): List<Visitor>
}