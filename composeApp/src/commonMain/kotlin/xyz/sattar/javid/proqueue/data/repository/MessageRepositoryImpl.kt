package xyz.sattar.javid.proqueue.data.repository

import xyz.sattar.javid.proqueue.data.localDataSource.dao.MessageDao
import xyz.sattar.javid.proqueue.data.localDataSource.mapper.toDomain
import xyz.sattar.javid.proqueue.data.localDataSource.mapper.toEntity
import xyz.sattar.javid.proqueue.domain.MessageRepository
import xyz.sattar.javid.proqueue.domain.model.Message

class MessageRepositoryImpl(
    private val messageDao: MessageDao
) : MessageRepository {
    override suspend fun insertMessage(message: Message): Boolean {
        return try {
            messageDao.insertMessage(message.toEntity())
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getAppointmentMessages(appointmentId: Long): List<Message> {
        return try {
            messageDao.getAppointmentMessages(appointmentId).map { it.toDomain() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getMessagesForVisitorAndBusiness(visitorId: Long, businessId: Long): List<Message> {
        return try {
            messageDao.getMessagesForVisitorAndBusiness(visitorId, businessId).map { it.toDomain() }
        } catch (e: Exception) {
            emptyList()
        }
    }
}