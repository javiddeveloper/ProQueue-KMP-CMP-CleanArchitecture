package xyz.sattar.javid.proqueue.feature.createVisitor

sealed class CreateVisitorEvent {
    object BackPressed : CreateVisitorEvent()
    data class VisitorCreated(val visitorId: Long) : CreateVisitorEvent()
}
