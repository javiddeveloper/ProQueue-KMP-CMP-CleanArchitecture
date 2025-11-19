package xyz.sattar.javid.proqueue

import android.app.Application
import xyz.sattar.javid.proqueue.di.initKoin

class AndroidApp: Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin(applicationContext)
    }
}