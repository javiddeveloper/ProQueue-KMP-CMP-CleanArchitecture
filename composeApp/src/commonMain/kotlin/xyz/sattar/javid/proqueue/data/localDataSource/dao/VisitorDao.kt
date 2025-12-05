package xyz.sattar.javid.proqueue.data.localDataSource.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import xyz.sattar.javid.proqueue.data.localDataSource.entity.VisitorEntity

@Dao
interface VisitorDao {
    @Upsert
    suspend fun upsertVisitor(visitor: VisitorEntity)

    @Query("SELECT * FROM Visitor WHERE phoneNumber = :phone")
    suspend fun getVisitorByPhone(phone: String): VisitorEntity?

    @Query("SELECT * FROM Visitor WHERE id = :visitorId")
    suspend fun getVisitorById(visitorId: Long): VisitorEntity?

    @Query("SELECT * FROM Visitor ORDER BY fullName ASC")
    suspend fun getAllVisitors(): List<VisitorEntity>

    @Query("DELETE FROM Visitor WHERE id = :visitorId")
    suspend fun deleteVisitor(visitorId: Long)
}

