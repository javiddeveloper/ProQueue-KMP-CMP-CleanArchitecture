package xyz.sattar.javid.proqueue.feature.lastVisitors

sealed interface LastVisitorsIntent {
    data object LoadVisitors : LastVisitorsIntent
    data class OnVisitorClick(val visitorId: String) : LastVisitorsIntent
}
