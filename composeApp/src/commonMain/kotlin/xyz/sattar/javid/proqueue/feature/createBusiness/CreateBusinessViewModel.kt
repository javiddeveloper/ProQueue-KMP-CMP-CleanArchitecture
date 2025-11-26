package xyz.sattar.javid.proqueue.feature.createBusiness

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import xyz.sattar.javid.proqueue.core.ui.BaseViewModel
import xyz.sattar.javid.proqueue.domain.model.Business
import xyz.sattar.javid.proqueue.domain.usecase.BusinessUpsertUseCase

class CreateBusinessViewModel(
    initialState: CreateBusinessState,
    private val businessUpsertUseCase: BusinessUpsertUseCase
) : BaseViewModel<CreateBusinessState, CreateBusinessState.PartialState, CreateBusinessEvent, CreateBusinessIntent>(
    initialState
) {
    override fun handleIntent(intent: CreateBusinessIntent): Flow<CreateBusinessState.PartialState> {
        return when (intent) {
            is CreateBusinessIntent.CreateBusiness -> createBusiness(
                intent.title,
                intent.phone,
                intent.address
            )
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
                currentState.copy(
                    businessCreated = false,
                    isLoading = false,
                    message = partialState.message
                )
        }
    }

    override fun createErrorState(message: String): CreateBusinessState.PartialState =
        CreateBusinessState.PartialState.ShowMessage(message)

    private fun createBusiness(
        businessName: String,
        phone: String,
        address: String
    ): Flow<CreateBusinessState.PartialState> = flow {
        emit(CreateBusinessState.PartialState.IsLoading(true))
        businessUpsertUseCase.invoke(
            Business(
                title = businessName,
                phone = phone,
                address = address,
                logoPath = "hfjdkfgdfg",
            )
        )
        emit(CreateBusinessState.PartialState.BusinessCreated)
    }
}