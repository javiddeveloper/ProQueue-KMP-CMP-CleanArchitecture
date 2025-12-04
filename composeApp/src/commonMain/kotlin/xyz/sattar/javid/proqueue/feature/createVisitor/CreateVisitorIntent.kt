package xyz.sattar.javid.proqueue.feature.createVisitor

sealed class CreateVisitorIntent {
    data class CreateVisitor(
        val fullName: String,
        val phoneNumber: String
    ) : CreateVisitorIntent()
    
    object BackPress : CreateVisitorIntent()
}
