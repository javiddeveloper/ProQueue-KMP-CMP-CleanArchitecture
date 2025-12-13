package xyz.sattar.javid.proqueue.feature.lastVisitors

sealed interface LastVisitorsIntent {
    data object LoadAppointments : LastVisitorsIntent
    data class OnAppointmentOptionsClick(val appointmentId: Long) : LastVisitorsIntent
    data object OnCreateAppointmentClick : LastVisitorsIntent
    data class OnEditAppointment(val appointmentId: Long) : LastVisitorsIntent
    data class OnDeleteAppointment(val appointmentId: Long) : LastVisitorsIntent
    data class OnMarkCompleted(val appointmentId: Long) : LastVisitorsIntent
    data class OnMarkNoShow(val appointmentId: Long) : LastVisitorsIntent
    data object DismissDialog : LastVisitorsIntent
}
