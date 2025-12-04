package xyz.sattar.javid.proqueue.feature.home

sealed interface HomeEvent {
    data object NavigateToCreateBusiness : HomeEvent
    data object NavigateToCreateVisitor : HomeEvent
}
