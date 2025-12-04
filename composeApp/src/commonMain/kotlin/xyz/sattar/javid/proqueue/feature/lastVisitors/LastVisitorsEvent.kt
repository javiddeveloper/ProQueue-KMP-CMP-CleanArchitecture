package xyz.sattar.javid.proqueue.feature.lastVisitors

sealed interface LastVisitorsEvent {
    data class NavigateToVisitorDetail(val visitorId: String) : LastVisitorsEvent
}
