package xyz.sattar.javid.proqueue.domain.model

data class Business(
    val id: Int = 0,
    val title: String,
    val phone: String,
    val address: String,
    val logoPath: String,
    val createTimeStamp: Long? = null,
)

