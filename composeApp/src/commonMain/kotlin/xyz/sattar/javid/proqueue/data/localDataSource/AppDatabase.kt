package xyz.sattar.javid.proqueue.data.localDataSource

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import xyz.sattar.javid.proqueue.data.localDataSource.dao.AppointmentDao
import xyz.sattar.javid.proqueue.data.localDataSource.dao.BusinessDao
import xyz.sattar.javid.proqueue.data.localDataSource.dao.MessageDao
import xyz.sattar.javid.proqueue.data.localDataSource.dao.VisitorDao
import xyz.sattar.javid.proqueue.data.localDataSource.entity.AppointmentEntity
import xyz.sattar.javid.proqueue.data.localDataSource.entity.BusinessEntity
import xyz.sattar.javid.proqueue.data.localDataSource.entity.MessageEntity
import xyz.sattar.javid.proqueue.data.localDataSource.entity.VisitorEntity

internal const val dbFileName = "proQueue.db"

@Database(
    entities = [
        BusinessEntity::class,
        VisitorEntity::class,
        AppointmentEntity::class,
        MessageEntity::class
    ],
    version = 2
)
@ConstructedBy(DbFactory::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun businessDao(): BusinessDao
    abstract fun visitorDao(): VisitorDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun messageDao(): MessageDao
}
