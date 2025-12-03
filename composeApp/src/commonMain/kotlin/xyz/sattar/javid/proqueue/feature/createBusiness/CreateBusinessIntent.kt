package xyz.sattar.javid.proqueue.feature.createBusiness

sealed class CreateBusinessIntent {
    data class CreateBusiness(
        val title: String,
        val phone: String,
        val address: String
    ) : CreateBusinessIntent()
    object LoadBusiness : CreateBusinessIntent()
    object BackPress : CreateBusinessIntent()
}