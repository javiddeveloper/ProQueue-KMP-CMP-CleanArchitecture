package xyz.sattar.javid.proqueue.feature.createBusiness

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import xyz.sattar.javid.proqueue.core.ui.BaseViewModel
import xyz.sattar.javid.proqueue.core.utils.FileStorage
import xyz.sattar.javid.proqueue.domain.model.Business
import xyz.sattar.javid.proqueue.domain.usecase.BusinessUpsertUseCase
import kotlinx.coroutines.launch

class CreateBusinessViewModel(
    initialState: CreateBusinessState,
    private val businessUpsertUseCase: BusinessUpsertUseCase,
    private val fileStorage: FileStorage
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
                    intent.defaultProgress,
                    intent.logoPath
                )
            }

            CreateBusinessIntent.BackPress -> sendEvent(CreateBusinessEvent.BackPressed)
            CreateBusinessIntent.BusinessCreated -> sendEvent(CreateBusinessEvent.NavigateToBusiness)
            is CreateBusinessIntent.OnImageSelected -> saveImage(intent.bytes)
        }
    }

    private fun saveImage(bytes: ByteArray?): Flow<CreateBusinessState.PartialState> = flow {
        if (bytes != null) {
            emit(CreateBusinessState.PartialState.IsLoading(true))
            try {
                val path = fileStorage.saveImage(bytes)
                emit(CreateBusinessState.PartialState.LogoSelected(path))
            } catch (e: Exception) {
                emit(CreateBusinessState.PartialState.ShowMessage(e.message ?: "Failed to save image"))
            } finally {
                emit(CreateBusinessState.PartialState.IsLoading(false))
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
        logoPath: String
    ): Flow<CreateBusinessState.PartialState> = flow {
        emit(CreateBusinessState.PartialState.IsLoading(true))
        businessUpsertUseCase.invoke(
            Business(
                title = businessName,
                phone = phone,
                address = address,
                logoPath = logoPath.ifEmpty { "Sample_path.jpg" },
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