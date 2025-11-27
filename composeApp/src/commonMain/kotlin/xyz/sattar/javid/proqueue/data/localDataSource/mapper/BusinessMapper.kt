package xyz.sattar.javid.proqueue.data.localDataSource.mapper

import xyz.sattar.javid.proqueue.data.localDataSource.entity.BusinessEntity
import xyz.sattar.javid.proqueue.domain.model.Business

fun BusinessEntity.toDomain() = Business(
    id = id,
    title = title,
    phone = phone,
    address = address,
    logoPath = logoPath,
    createTimeStamp = createTimeStamp,
)

fun Business.toEntity() = BusinessEntity(
    id = id,
    title = title,
    phone = phone,
    address = address,
    logoPath = logoPath,
    createTimeStamp = createTimeStamp,
)

