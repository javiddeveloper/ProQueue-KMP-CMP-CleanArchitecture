package xyz.sattar.javid.proqueue.feature.lastVisitors

import androidx.compose.runtime.Immutable

@Immutable
data class LastVisitorsState(
    val isLoading: Boolean = false,
    val visitors: List<VisitorItem> = emptyList(),
    val message: String? = null
) {
    sealed class PartialState {
        data class IsLoading(val isLoading: Boolean) : PartialState()
        data class ShowMessage(val message: String) : PartialState()
        data class LoadVisitors(val visitors: List<VisitorItem>) : PartialState()
    }
}

data class VisitorItem(
    val id: String,
    val name: String,
    val phone: String,
    val visitDate: String
)
