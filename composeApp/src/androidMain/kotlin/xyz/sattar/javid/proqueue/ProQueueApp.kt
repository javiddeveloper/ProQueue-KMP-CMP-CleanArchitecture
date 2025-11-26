package xyz.sattar.javid.proqueue

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import xyz.sattar.javid.proqueue.di.appModule
import xyz.sattar.javid.proqueue.di.dbModuleAndroid

class ProQueueApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(applicationContext)
            modules(dbModuleAndroid, appModule)
        }
    }
}