package xyz.sattar.javid.proqueue.domain

interface VisitorRepository {
    fun upsertVisitor():Boolean
    fun upsertBusiness():Boolean

}