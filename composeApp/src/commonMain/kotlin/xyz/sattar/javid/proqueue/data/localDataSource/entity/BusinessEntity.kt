package xyz.sattar.javid.proqueue.data.localDataSource.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Business")
data class BusinessEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String = "",
    val phone: String = "",
    val address: String = "",
    val logoPath: String = "",
    val createTimeStamp: Long? = 0,
)

