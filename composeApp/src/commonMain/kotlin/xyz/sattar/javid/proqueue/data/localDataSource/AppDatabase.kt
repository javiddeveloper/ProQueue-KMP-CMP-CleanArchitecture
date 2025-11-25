package xyz.sattar.javid.proqueue.data.localDataSource

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import xyz.sattar.javid.proqueue.data.localDataSource.dao.BusinessDao
import xyz.sattar.javid.proqueue.data.localDataSource.entity.BusinessEntity
import xyz.sattar.javid.proqueue.data.localDataSource.entity.VisitorEntity

internal const val dbFileName = "proQueue.db"

@Database(
    entities = [BusinessEntity::class, VisitorEntity::class],
    version = 1
)
@ConstructedBy(DbFactory::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun businessDao(): BusinessDao
}

