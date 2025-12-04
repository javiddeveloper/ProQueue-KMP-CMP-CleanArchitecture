package xyz.sattar.javid.proqueue.feature.home

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import xyz.sattar.javid.proqueue.core.ui.BaseViewModel

class HomeViewModel : BaseViewModel<HomeState, HomeState.PartialState, HomeEvent, HomeIntent>(
    initialState = HomeState()
) {
    override fun handleIntent(intent: HomeIntent): Flow<HomeState.PartialState> {
        return when (intent) {
            HomeIntent.LoadData -> loadData()
            HomeIntent.NavigateToCreateBusiness -> sendEvent(HomeEvent.NavigateToCreateBusiness)
            HomeIntent.NavigateToCreateVisitor -> sendEvent(HomeEvent.NavigateToCreateVisitor)
        }
    }

    override fun reduceState(
        currentState: HomeState,
        partialState: HomeState.PartialState
    ): HomeState {
        return when (partialState) {
            is HomeState.PartialState.IsLoading ->
                currentState.copy(isLoading = partialState.isLoading)
            is HomeState.PartialState.ShowMessage ->
                currentState.copy(message = partialState.message, isLoading = false)
            is HomeState.PartialState.LoadBusinessName ->
                currentState.copy(businessName = partialState.businessName, isLoading = false)
        }
    }

    override fun createErrorState(message: String): HomeState.PartialState =
        HomeState.PartialState.ShowMessage(message)

    private fun loadData(): Flow<HomeState.PartialState> = flow {
        emit(HomeState.PartialState.IsLoading(true))
        // TODO: Load business data from repository
        emit(HomeState.PartialState.IsLoading(false))
    }
}
