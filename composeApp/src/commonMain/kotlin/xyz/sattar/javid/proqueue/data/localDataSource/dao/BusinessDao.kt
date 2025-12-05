package xyz.sattar.javid.proqueue.data.localDataSource.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import xyz.sattar.javid.proqueue.data.localDataSource.entity.BusinessEntity

@Dao
interface BusinessDao {
    @Upsert
    suspend fun upsertBusiness(business: BusinessEntity)

    @Query("SELECT * FROM Business ORDER BY createdAt DESC")
    suspend fun loadAllBusiness(): List<BusinessEntity>

    @Query("SELECT * FROM Business WHERE id = :businessId")
    suspend fun getBusinessById(businessId: Long): BusinessEntity?

    @Query("DELETE FROM Business WHERE id = :businessId")
    suspend fun deleteBusiness(businessId: Long)
}

