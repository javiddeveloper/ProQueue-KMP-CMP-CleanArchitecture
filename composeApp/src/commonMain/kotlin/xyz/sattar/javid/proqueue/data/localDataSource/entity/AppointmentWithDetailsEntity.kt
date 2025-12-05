package xyz.sattar.javid.proqueue.data.localDataSource.entity

import androidx.room.Embedded
import androidx.room.Relation

data class AppointmentWithDetailsEntity(
    @Embedded val appointment: AppointmentEntity,
    @Relation(
        parentColumn = "visitorId",
        entityColumn = "id"
    )
    val visitor: VisitorEntity,
    @Relation(
        parentColumn = "businessId",
        entityColumn = "id"
    )
    val business: BusinessEntity
)