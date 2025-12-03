package xyz.sattar.javid.proqueue.feature.createBusiness

sealed class CreateBusinessEvent {
    object NavigateToVisitors : CreateBusinessEvent()
    object BackPressed : CreateBusinessEvent()
}