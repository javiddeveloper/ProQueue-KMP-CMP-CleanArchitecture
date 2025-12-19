package xyz.sattar.javid.proqueue.feature.settings

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import xyz.sattar.javid.proqueue.core.ui.BaseViewModel
import xyz.sattar.javid.proqueue.domain.usecase.DeleteBusinessUseCase

class SettingsViewModel(
    private val deleteBusinessUseCase: DeleteBusinessUseCase
) : BaseViewModel<SettingsState, SettingsState.PartialState, SettingsEvent, SettingsIntent>(
    initialState = SettingsState()
) {
    override fun handleIntent(intent: SettingsIntent): Flow<SettingsState.PartialState> {
        return when (intent) {
            SettingsIntent.LoadSettings -> loadSettings()
            SettingsIntent.OnAboutClick -> sendEvent(SettingsEvent.NavigateToAbout)
            SettingsIntent.OnChangeBusinessClick -> sendEvent(SettingsEvent.NavigateToBusinessSelection)
            SettingsIntent.OnDeleteBusinessClick -> deleteBusiness()
            SettingsIntent.OnNotificationsClick -> sendEvent(SettingsEvent.NavigateToNotifications)
            SettingsIntent.OnMessagesClick -> sendEvent(SettingsEvent.NavigateToMessages)
        }
    }

    private fun deleteBusiness(): Flow<SettingsState.PartialState> = flow {
        val currentBusiness = xyz.sattar.javid.proqueue.core.state.BusinessStateHolder.selectedBusiness.value
        if (currentBusiness != null) {
            emit(SettingsState.PartialState.IsLoading(true))
            try {
                deleteBusinessUseCase(currentBusiness.id)
                sendEvent(SettingsEvent.BusinessDeleted)
            } catch (e: Exception) {
                emit(SettingsState.PartialState.ShowMessage(e.message ?: "Error deleting business"))
            } finally {
                emit(SettingsState.PartialState.IsLoading(false))
            }
        }
    }

    override fun reduceState(
        currentState: SettingsState,
        partialState: SettingsState.PartialState
    ): SettingsState {
        return when (partialState) {
            is SettingsState.PartialState.IsLoading ->
                currentState.copy(isLoading = partialState.isLoading)
            is SettingsState.PartialState.ShowMessage ->
                currentState.copy(message = partialState.message, isLoading = false)
            is SettingsState.PartialState.LoadSettings ->
                currentState.copy(businessName = partialState.businessName, isLoading = false)
        }
    }

    override fun createErrorState(message: String): SettingsState.PartialState =
        SettingsState.PartialState.ShowMessage(message)

    private fun loadSettings(): Flow<SettingsState.PartialState> = flow {
        emit(SettingsState.PartialState.IsLoading(true))
        // TODO: Load settings from repository
        emit(SettingsState.PartialState.IsLoading(false))
    }
}
