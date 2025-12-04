package xyz.sattar.javid.proqueue.feature.businessList

import xyz.sattar.javid.proqueue.domain.model.Business

sealed interface BusinessListEvent {
    data class NavigateToMain(val business: Business) : BusinessListEvent
    data object NavigateToCreateBusiness : BusinessListEvent
}
