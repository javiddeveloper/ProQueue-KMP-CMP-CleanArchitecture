package xyz.sattar.javid.proqueue.feature.settings

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import xyz.sattar.javid.proqueue.core.ui.BaseViewModel

class SettingsViewModel : BaseViewModel<SettingsState, SettingsState.PartialState, SettingsEvent, SettingsIntent>(
    initialState = SettingsState()
) {
    override fun handleIntent(intent: SettingsIntent): Flow<SettingsState.PartialState> {
        return when (intent) {
            SettingsIntent.LoadSettings -> loadSettings()
            SettingsIntent.OnAboutClick -> sendEvent(SettingsEvent.NavigateToAbout)
            SettingsIntent.OnLogoutClick -> sendEvent(SettingsEvent.Logout)
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
