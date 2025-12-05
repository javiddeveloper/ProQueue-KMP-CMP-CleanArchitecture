package xyz.sattar.javid.proqueue.data.localDataSource.mapper

import xyz.sattar.javid.proqueue.data.localDataSource.entity.MessageEntity
import xyz.sattar.javid.proqueue.domain.model.Message

fun MessageEntity.toDomain() = Message(
    id = id,
    appointmentId = appointmentId,
    messageType = messageType,
    content = content,
    sentAt = sentAt,
)

fun Message.toEntity() = MessageEntity(
    id = id,
    appointmentId = appointmentId,
    messageType = messageType,
    content = content,
    sentAt = sentAt,
)

