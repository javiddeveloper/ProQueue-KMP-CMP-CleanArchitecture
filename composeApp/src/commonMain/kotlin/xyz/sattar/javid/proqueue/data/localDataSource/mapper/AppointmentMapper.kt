package xyz.sattar.javid.proqueue.data.localDataSource.mapper

import xyz.sattar.javid.proqueue.data.localDataSource.entity.AppointmentEntity
import xyz.sattar.javid.proqueue.data.localDataSource.entity.AppointmentWithDetailsEntity
import xyz.sattar.javid.proqueue.domain.model.Appointment
import xyz.sattar.javid.proqueue.domain.model.AppointmentWithDetails

fun AppointmentEntity.toDomain() = Appointment(
    id = id,
    businessId = businessId,
    visitorId = visitorId,
    appointmentDate = appointmentDate,
    serviceDuration = serviceDuration,
    status = status,
    description = description,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun Appointment.toEntity() = AppointmentEntity(
    id = id,
    businessId = businessId,
    visitorId = visitorId,
    appointmentDate = appointmentDate,
    serviceDuration = serviceDuration,
    status = status,
    description = description,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun AppointmentWithDetailsEntity.toDomain() = AppointmentWithDetails(
    appointment = appointment.toDomain(),
    visitor = visitor.toDomain(),
    business = business.toDomain()
)
