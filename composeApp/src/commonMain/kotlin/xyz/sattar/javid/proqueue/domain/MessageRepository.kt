package xyz.sattar.javid.proqueue.domain

import xyz.sattar.javid.proqueue.domain.model.Message

interface MessageRepository {
    suspend fun insertMessage(message: Message): Boolean
    suspend fun getAppointmentMessages(appointmentId: Long): List<Message>
}