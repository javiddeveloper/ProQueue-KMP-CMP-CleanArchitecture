package xyz.sattar.javid.proqueue.feature.createVisitor

sealed class CreateVisitorEvent {
    object NavigateToQueue : CreateVisitorEvent()
    object BackPressed : CreateVisitorEvent()
}
