package xyz.sattar.javid.proqueue.feature.createBusiness

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import xyz.sattar.javid.proqueue.core.ui.BaseViewModel
import xyz.sattar.javid.proqueue.domain.model.Business
import xyz.sattar.javid.proqueue.domain.usecase.BusinessUpsertUseCase
import xyz.sattar.javid.proqueue.domain.usecase.LoadBusinessUseCase

class CreateBusinessViewModel(
    initialState: CreateBusinessState,
    private val businessUpsertUseCase: BusinessUpsertUseCase,
    private val loadBusinessUseCase: LoadBusinessUseCase
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

            CreateBusinessIntent.LoadBusiness -> loadBusiness()
            CreateBusinessIntent.BackPress -> {
                sendEvent(CreateBusinessEvent.BackPressed)
            }
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

            is CreateBusinessState.PartialState.LoadLastBusiness ->
                currentState.copy(
                    businessCreated = false,
                    isLoading = false,
                    message = null,
                    business = partialState.business
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
                logoPath = "Sample_path.jpg",
            )
        )
        emit(CreateBusinessState.PartialState.BusinessCreated)
    }


    private fun loadBusiness(): Flow<CreateBusinessState.PartialState> = flow {
        emit(CreateBusinessState.PartialState.IsLoading(true))
        emit(CreateBusinessState.PartialState.LoadLastBusiness(loadBusinessUseCase.invoke()))
    }

}