package xyz.sattar.javid.proqueue.feature.createBusiness

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import xyz.sattar.javid.proqueue.core.ui.BaseViewModel
import xyz.sattar.javid.proqueue.domain.model.Business
import xyz.sattar.javid.proqueue.domain.usecase.BusinessUpsertUseCase

class CreateBusinessViewModel(
    savedStateHandle: SavedStateHandle,
    initialState: CreateBusinessState,
    private val businessUpsertUseCase: BusinessUpsertUseCase
) : BaseViewModel<CreateBusinessState, CreateBusinessState.PartialState, CreateBusinessEvent, CreateBusinessIntent>(
    savedStateHandle,
    initialState
) {
    override fun handleIntent(intent: CreateBusinessIntent): Flow<CreateBusinessState.PartialState> {
       return when (intent) {
            is CreateBusinessIntent.CreateBusiness -> createBusiness(intent.businessName)
        }
    }

    override fun reduceState(
        currentState: CreateBusinessState,
        partialState: CreateBusinessState.PartialState
    ): CreateBusinessState {
        return when (partialState) {
            CreateBusinessState.PartialState.BusinessCreated ->
                currentState.copy(businessCreated = true, isLoading = false, message = null)
            is CreateBusinessState.PartialState.IsLoading ->
                currentState.copy(businessCreated = false, isLoading = true, message = null)
            is CreateBusinessState.PartialState.ShowMessage ->
                currentState.copy(businessCreated = false, isLoading = false, message = partialState.message)
        }
    }

    override fun createErrorState(message: String): CreateBusinessState.PartialState =
        CreateBusinessState.PartialState.ShowMessage(message)

    private fun createBusiness(businessName: String) : Flow<CreateBusinessState.PartialState> = flow {
        emit(CreateBusinessState.PartialState.IsLoading(true))
        businessUpsertUseCase.invoke(
            Business(
                title = businessName,
                phone = "090909090",
                address = "aaaaa",
                logoPath = "hfjdkfgdfg",
            )
        )
        emit(CreateBusinessState.PartialState.BusinessCreated)
    }
}