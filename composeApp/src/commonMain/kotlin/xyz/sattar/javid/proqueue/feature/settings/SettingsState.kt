package xyz.sattar.javid.proqueue.feature.settings

import androidx.compose.runtime.Immutable

@Immutable
data class SettingsState(
    val isLoading: Boolean = false,
    val businessName: String? = null,
    val appVersion: String = "1.3.0",
    val message: String? = null
) {
    sealed class PartialState {
        data class IsLoading(val isLoading: Boolean) : PartialState()
        data class ShowMessage(val message: String) : PartialState()
        data class LoadSettings(val businessName: String?) : PartialState()
    }
}
