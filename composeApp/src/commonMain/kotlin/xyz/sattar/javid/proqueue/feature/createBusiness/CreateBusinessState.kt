package xyz.sattar.javid.proqueue.feature.createBusiness

import androidx.compose.runtime.Immutable
import xyz.sattar.javid.proqueue.domain.model.Business

@Immutable
data class CreateBusinessState (
    val isLoading: Boolean = false,
    val businessCreated: Boolean = false,
    val business: Business? = null,
    val message: String? = null
){
    sealed class PartialState{
        data class IsLoading(val isLoading: Boolean): PartialState()
        data class LoadLastBusiness(val business: Business?): PartialState()
        data class ShowMessage(val message: String): PartialState()
        object BusinessCreated: PartialState()
    }
}