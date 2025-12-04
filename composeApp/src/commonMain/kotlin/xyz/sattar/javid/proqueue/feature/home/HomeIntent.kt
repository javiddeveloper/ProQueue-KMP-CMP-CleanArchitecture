package xyz.sattar.javid.proqueue.feature.home

sealed interface HomeIntent {
    data object LoadData : HomeIntent
    data object NavigateToCreateBusiness : HomeIntent
    data object NavigateToCreateVisitor : HomeIntent
}
