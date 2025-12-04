package xyz.sattar.javid.proqueue.feature.businessList

import xyz.sattar.javid.proqueue.domain.model.Business

sealed interface BusinessListIntent {
    data object LoadBusinesses : BusinessListIntent
    data class OnBusinessClick(val business: Business) : BusinessListIntent
    data object OnCreateBusinessClick : BusinessListIntent
}
