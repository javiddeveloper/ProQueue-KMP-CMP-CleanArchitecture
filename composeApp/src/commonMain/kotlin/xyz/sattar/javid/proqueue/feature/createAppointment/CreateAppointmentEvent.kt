package xyz.sattar.javid.proqueue.feature.createAppointment

sealed interface CreateAppointmentEvent {
    data object NavigateBack : CreateAppointmentEvent
    data object AppointmentCreated : CreateAppointmentEvent
}
