package xyz.sattar.javid.proqueue.feature.createVisitor

import androidx.compose.runtime.Immutable

@Immutable
data class CreateVisitorState(
    val isLoading: Boolean = false,
    val visitorCreated: Boolean = false,
    val message: String? = null
) {
    sealed class PartialState {
        data class IsLoading(val isLoading: Boolean) : PartialState()
        data class ShowMessage(val message: String) : PartialState()
        object VisitorCreated : PartialState()
    }
}

data class CreateVisitorErrors(
    val fullName: String? = null,
    val phoneNumber: String? = null
)
