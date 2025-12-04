package xyz.sattar.javid.proqueue.feature.lastVisitors

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import xyz.sattar.javid.proqueue.core.ui.BaseViewModel

class LastVisitorsViewModel : BaseViewModel<LastVisitorsState, LastVisitorsState.PartialState, LastVisitorsEvent, LastVisitorsIntent>(
    initialState = LastVisitorsState()
) {
    override fun handleIntent(intent: LastVisitorsIntent): Flow<LastVisitorsState.PartialState> {
        return when (intent) {
            LastVisitorsIntent.LoadVisitors -> loadVisitors()
            is LastVisitorsIntent.OnVisitorClick -> sendEvent(
                LastVisitorsEvent.NavigateToVisitorDetail(intent.visitorId)
            )
        }
    }

    override fun reduceState(
        currentState: LastVisitorsState,
        partialState: LastVisitorsState.PartialState
    ): LastVisitorsState {
        return when (partialState) {
            is LastVisitorsState.PartialState.IsLoading ->
                currentState.copy(isLoading = partialState.isLoading)
            is LastVisitorsState.PartialState.ShowMessage ->
                currentState.copy(message = partialState.message, isLoading = false)
            is LastVisitorsState.PartialState.LoadVisitors ->
                currentState.copy(visitors = partialState.visitors, isLoading = false)
        }
    }

    override fun createErrorState(message: String): LastVisitorsState.PartialState =
        LastVisitorsState.PartialState.ShowMessage(message)

    private fun loadVisitors(): Flow<LastVisitorsState.PartialState> = flow {
        emit(LastVisitorsState.PartialState.IsLoading(true))
        // TODO: Load visitors from repository
        // For now, using mock data
        val mockVisitors = listOf(
            VisitorItem("1", "علی احمدی", "09121234567", "1403/09/13 - 14:30"),
            VisitorItem("2", "سارا محمدی", "09129876543", "1403/09/13 - 15:00"),
            VisitorItem("3", "رضا کریمی", "09123456789", "1403/09/12 - 10:15")
        )
        emit(LastVisitorsState.PartialState.LoadVisitors(mockVisitors))
    }
}
