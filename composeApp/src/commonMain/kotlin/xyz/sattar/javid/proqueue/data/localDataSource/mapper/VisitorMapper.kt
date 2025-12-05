package xyz.sattar.javid.proqueue.data.localDataSource.mapper

import xyz.sattar.javid.proqueue.data.localDataSource.entity.VisitorEntity
import xyz.sattar.javid.proqueue.domain.model.Visitor

fun VisitorEntity.toDomain() = Visitor(
    id = id,
    fullName = fullName,
    phoneNumber = phoneNumber,
    createdAt = createdAt,
)

fun Visitor.toEntity() = VisitorEntity(
    id = id,
    fullName = fullName,
    phoneNumber = phoneNumber,
    createdAt = createdAt,
)

