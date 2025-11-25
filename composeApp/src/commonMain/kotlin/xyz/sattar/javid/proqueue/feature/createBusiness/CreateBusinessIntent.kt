package xyz.sattar.javid.proqueue.feature.createBusiness

sealed class CreateBusinessIntent {
    data class CreateBusiness(val businessName:String): CreateBusinessIntent()
}