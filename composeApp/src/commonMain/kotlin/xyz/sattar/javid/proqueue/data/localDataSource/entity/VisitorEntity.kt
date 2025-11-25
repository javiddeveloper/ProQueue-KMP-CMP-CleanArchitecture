package xyz.sattar.javid.proqueue.data.localDataSource.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Visitor")
data class VisitorEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fullName: String = "",
    val phoneNumber: String = "",
    val statusInQueue: Int = -1,
    val updateTimeStamp: Long = 0,
)

