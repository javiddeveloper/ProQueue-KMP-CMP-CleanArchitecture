package xyz.sattar.javid.proqueue.feature.home

import androidx.compose.runtime.Immutable

@Immutable
data class HomeState(
    val isLoading: Boolean = false,
    val businessName: String? = null,
    val message: String? = null
) {
    sealed class PartialState {
        data class IsLoading(val isLoading: Boolean) : PartialState()
        data class ShowMessage(val message: String) : PartialState()
        data class LoadBusinessName(val businessName: String?) : PartialState()
    }
}
