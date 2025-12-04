package xyz.sattar.javid.proqueue.feature.home

import androidx.compose.runtime.Immutable
import xyz.sattar.javid.proqueue.domain.model.Business

@Immutable
data class HomeState(
    val isLoading: Boolean = false,
    val business: Business? = null,
    val message: String? = null
) {
    sealed class PartialState {
        data class IsLoading(val isLoading: Boolean) : PartialState()
        data class ShowMessage(val message: String) : PartialState()
        data class LoadBusinessName(val business: Business?) : PartialState()
    }
}
