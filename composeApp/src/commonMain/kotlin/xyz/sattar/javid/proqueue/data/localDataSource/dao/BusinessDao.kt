package xyz.sattar.javid.proqueue.data.localDataSource.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import xyz.sattar.javid.proqueue.data.localDataSource.entity.BusinessEntity
import xyz.sattar.javid.proqueue.data.localDataSource.entity.VisitorEntity

@Dao
interface BusinessDao {

    @Upsert
    suspend fun upsertVisitor(visitorEntity: VisitorEntity)

    @Upsert
    suspend fun upsertBusiness(business: BusinessEntity)

    @Query("SELECT * FROM Business ORDER BY createTimeStamp DESC")
    suspend fun loadAllBusiness(): List<BusinessEntity>

    @Query("SELECT * FROM Visitor Where statusInQueue=0 ORDER BY updateTimeStamp DESC")
    suspend fun getWaitingVisitor(): List<VisitorEntity>

    @Query("DELETE FROM visitor where id=:visitorId")
    suspend fun deleteVisitor(visitorId:Int)

    @Query("DELETE FROM Business WHERE id = :businessId")
    suspend fun deleteBusiness(businessId: Int)

    @Query("DELETE FROM Visitor")
    suspend fun deleteAllVisitors()

    @Transaction
    suspend fun deleteBusinessAndQueue(businessId: Int) {
        deleteBusiness(businessId)
    }


}

