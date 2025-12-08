package xyz.sattar.javid.proqueue.feature.visitorSelection

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import xyz.sattar.javid.proqueue.core.ui.BaseViewModel
import xyz.sattar.javid.proqueue.domain.usecase.DeleteVisitorUseCase
import xyz.sattar.javid.proqueue.domain.usecase.GetAllVisitorsUseCase

class VisitorSelectionViewModel(
    private val getAllVisitorsUseCase: GetAllVisitorsUseCase,
    private val deleteVisitorUseCase: DeleteVisitorUseCase
) : BaseViewModel<VisitorSelectionState, VisitorSelectionState.PartialState, VisitorSelectionEvent, VisitorSelectionIntent>(
    initialState = VisitorSelectionState()
) {
    override fun handleIntent(intent: VisitorSelectionIntent): Flow<VisitorSelectionState.PartialState> {
        return when (intent) {
            VisitorSelectionIntent.LoadVisitors -> loadVisitors()
            is VisitorSelectionIntent.SearchVisitors -> flow {
                emit(VisitorSelectionState.PartialState.UpdateSearchQuery(intent.query))
            }
            is VisitorSelectionIntent.SelectVisitor -> {
                sendEvent(VisitorSelectionEvent.NavigateToCreateAppointment(intent.visitorId))
            }
            VisitorSelectionIntent.CreateNewVisitor -> {
                sendEvent(VisitorSelectionEvent.NavigateToCreateVisitor)
            }
            is VisitorSelectionIntent.DeleteVisitor -> deleteVisitor(intent.visitorId)
            is VisitorSelectionIntent.EditVisitor -> {
                sendEvent(VisitorSelectionEvent.NavigateToEditVisitor(intent.visitorId))
            }
            VisitorSelectionIntent.BackPress -> {
                sendEvent(VisitorSelectionEvent.NavigateBack)
            }
        }
    }

    override fun reduceState(
        currentState: VisitorSelectionState,
        partialState: VisitorSelectionState.PartialState
    ): VisitorSelectionState {
        return when (partialState) {
            is VisitorSelectionState.PartialState.IsLoading ->
                currentState.copy(isLoading = partialState.isLoading)
            is VisitorSelectionState.PartialState.LoadVisitors ->
                currentState.copy(
                    visitors = partialState.visitors,
                    filteredVisitors = filterVisitors(partialState.visitors, currentState.searchQuery),
                    isLoading = false
                )
            is VisitorSelectionState.PartialState.UpdateSearchQuery ->
                currentState.copy(
                    searchQuery = partialState.query,
                    filteredVisitors = filterVisitors(currentState.visitors, partialState.query)
                )
            is VisitorSelectionState.PartialState.ShowMessage ->
                currentState.copy(message = partialState.message, isLoading = false)
        }
    }

    override fun createErrorState(message: String): VisitorSelectionState.PartialState =
        VisitorSelectionState.PartialState.ShowMessage(message)

    private fun loadVisitors(): Flow<VisitorSelectionState.PartialState> = flow {
        emit(VisitorSelectionState.PartialState.IsLoading(true))
        try {
            val visitors = getAllVisitorsUseCase()
            emit(VisitorSelectionState.PartialState.LoadVisitors(visitors))
        } catch (e: Exception) {
            emit(VisitorSelectionState.PartialState.ShowMessage(e.message ?: "خطا در بارگذاری لیست مراجعین"))
        }
    }

    private fun deleteVisitor(visitorId: Long): Flow<VisitorSelectionState.PartialState> = flow {
        emit(VisitorSelectionState.PartialState.IsLoading(true))
        try {
            deleteVisitorUseCase(visitorId)
            val visitors = getAllVisitorsUseCase()
            emit(VisitorSelectionState.PartialState.LoadVisitors(visitors))
        } catch (e: Exception) {
            emit(VisitorSelectionState.PartialState.ShowMessage(e.message ?: "خطا در حذف مراجع"))
        }
    }

    private fun filterVisitors(visitors: List<xyz.sattar.javid.proqueue.domain.model.Visitor>, query: String): List<xyz.sattar.javid.proqueue.domain.model.Visitor> {
        if (query.isBlank()) return visitors
        return visitors.filter {
            it.fullName.contains(query, ignoreCase = true) ||
            it.phoneNumber.contains(query)
        }
    }
}
