package xyz.sattar.javid.proqueue.feature.businessList

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import xyz.sattar.javid.proqueue.core.ui.BaseViewModel
import xyz.sattar.javid.proqueue.domain.usecase.LoadAllBusinessUseCase

class BusinessListViewModel(
    private val loadAllBusinessUseCase: LoadAllBusinessUseCase
) : BaseViewModel<BusinessListState, BusinessListState.PartialState, BusinessListEvent, BusinessListIntent>(
    initialState = BusinessListState()
) {
    override fun handleIntent(intent: BusinessListIntent): Flow<BusinessListState.PartialState> {
        return when (intent) {
            BusinessListIntent.LoadBusinesses -> loadBusinesses()
            is BusinessListIntent.OnBusinessClick -> {
                sendEvent(BusinessListEvent.NavigateToMain(intent.business))
                flow { } // No state change needed immediately, navigation happens via event
            }
            BusinessListIntent.OnCreateBusinessClick -> {
                sendEvent(BusinessListEvent.NavigateToCreateBusiness)
                flow { }
            }
        }
    }

    override fun reduceState(
        currentState: BusinessListState,
        partialState: BusinessListState.PartialState
    ): BusinessListState {
        return when (partialState) {
            is BusinessListState.PartialState.IsLoading ->
                currentState.copy(isLoading = partialState.isLoading)
            is BusinessListState.PartialState.ShowMessage ->
                currentState.copy(message = partialState.message, isLoading = false)
            is BusinessListState.PartialState.LoadBusinesses ->
                currentState.copy(businesses = partialState.businesses, isLoading = false)
        }
    }

    override fun createErrorState(message: String): BusinessListState.PartialState =
        BusinessListState.PartialState.ShowMessage(message)

    private fun loadBusinesses(): Flow<BusinessListState.PartialState> = flow {
        emit(BusinessListState.PartialState.IsLoading(true))
        try {
            val businesses = loadAllBusinessUseCase()
            emit(BusinessListState.PartialState.LoadBusinesses(businesses))
        } catch (e: Exception) {
            emit(BusinessListState.PartialState.ShowMessage(e.message ?: "Unknown error"))
        }
    }
}
