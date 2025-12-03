package xyz.sattar.javid.proqueue.domain

import xyz.sattar.javid.proqueue.domain.model.Visitor

interface VisitorRepository {
    suspend fun upsertVisitor(visitor: Visitor): Boolean
}