package xyz.sattar.javid.proqueue.data.localDataSource.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import xyz.sattar.javid.proqueue.data.localDataSource.entity.MessageEntity

@Dao
interface MessageDao {
    @Insert
    suspend fun insertMessage(message: MessageEntity)

    @Query("SELECT * FROM Message WHERE appointmentId = :appointmentId ORDER BY sentAt DESC")
    suspend fun getAppointmentMessages(appointmentId: Long): List<MessageEntity>
}