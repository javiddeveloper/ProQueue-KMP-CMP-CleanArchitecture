package xyz.sattar.javid.proqueue.di

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

actual fun initKoin(platformContext: Any?) {
    val appContext = platformContext as? Context
        ?: throw IllegalArgumentException("Android Context must be provided to initKoin on Android")

    startKoin {
        androidContext(appContext)
        modules(listOf(commonModule, androidModule))
    }
}
