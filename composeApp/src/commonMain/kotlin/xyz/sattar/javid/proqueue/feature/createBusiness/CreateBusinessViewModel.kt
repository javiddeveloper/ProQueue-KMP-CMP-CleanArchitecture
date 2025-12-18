package xyz.sattar.javid.proqueue.feature.createBusiness

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import xyz.sattar.javid.proqueue.core.ui.BaseViewModel
import xyz.sattar.javid.proqueue.domain.model.Business
import xyz.sattar.javid.proqueue.domain.usecase.BusinessUpsertUseCase
import kotlinx.coroutines.launch

class CreateBusinessViewModel(
    initialState: CreateBusinessState,
    private val businessUpsertUseCase: BusinessUpsertUseCase,
) : BaseViewModel<CreateBusinessState, CreateBusinessState.PartialState, CreateBusinessEvent, CreateBusinessIntent>(
    initialState
) {
    override fun handleIntent(intent: CreateBusinessIntent): Flow<CreateBusinessState.PartialState> {
        return when (intent) {
            is CreateBusinessIntent.CreateBusiness -> {
                createBusiness(
                    intent.title,
                    intent.phone,
                    intent.address,
                    intent.defaultProgress
                )
            }

            CreateBusinessIntent.BackPress -> sendEvent(CreateBusinessEvent.BackPressed)
            CreateBusinessIntent.BusinessCreated -> sendEvent(CreateBusinessEvent.NavigateToBusiness)
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
                currentState.copy(businessCreated = false, isLoading = partialState.isLoading, message = null)

            is CreateBusinessState.PartialState.ShowMessage ->
                currentState.copy(
                    businessCreated = false,
                    isLoading = false,
                    message = partialState.message
                )
            is CreateBusinessState.PartialState.LogoSelected ->
                currentState.copy(logoPath = partialState.path, isLoading = false)
        }
    }

    override fun createErrorState(message: String): CreateBusinessState.PartialState =
        CreateBusinessState.PartialState.ShowMessage(message)

    private fun createBusiness(
        businessName: String,
        phone: String,
        address: String,
        defaultProgress: String,
    ): Flow<CreateBusinessState.PartialState> = flow {
        emit(CreateBusinessState.PartialState.IsLoading(true))
        businessUpsertUseCase.invoke(
            Business(
                title = businessName,
                phone = phone,
                address = address,
                logoPath = uiState.value.logoPath ?: "Sample_path.jpg",
                id = 0,
                defaultServiceDuration = defaultProgress.toIntOrNull() ?: 15,
                workStartHour = 9,
                workEndHour = 17,
                notificationEnabled = false,
                notificationTypes = "",
            )
        )
        emit(CreateBusinessState.PartialState.BusinessCreated)
    }
}
