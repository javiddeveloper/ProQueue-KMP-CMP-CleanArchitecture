package xyz.sattar.javid.proqueue.feature.home

sealed interface HomeIntent {
    data object LoadData : HomeIntent
    data object NavigateToCreateBusiness : HomeIntent
    data object NavigateToCreateVisitor : HomeIntent
    data class RemoveAppointment(val appointmentId: Long) : HomeIntent
    data class MarkAppointmentCompleted(val appointmentId: Long) : HomeIntent
    data class MarkAppointmentNoShow(val appointmentId: Long) : HomeIntent
    data class SendMessage(val appointmentId: Long, val type: String) : HomeIntent
}
