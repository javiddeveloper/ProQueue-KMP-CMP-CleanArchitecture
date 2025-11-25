package xyz.sattar.javid.proqueue.domain.model

data class Visitor(
    val id: Int,
    val fullName: String,
    val phoneNumber: String,
    val statusInQueue: Int,
    val updateTimeStamp: Long,
)

