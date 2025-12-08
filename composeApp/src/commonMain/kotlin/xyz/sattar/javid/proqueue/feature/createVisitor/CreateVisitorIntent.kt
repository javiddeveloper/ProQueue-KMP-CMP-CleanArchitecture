package xyz.sattar.javid.proqueue.feature.createVisitor

sealed class CreateVisitorIntent {
    data class CreateVisitor(
        val fullName: String,
        val phoneNumber: String,
        val id: Long = 0 // Add id for update
    ) : CreateVisitorIntent()
    
    data class LoadVisitor(val visitorId: Long) : CreateVisitorIntent()
    
    object BackPress : CreateVisitorIntent()
}
