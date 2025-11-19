package xyz.sattar.javid.proqueue.di

import org.koin.core.context.startKoin

actual fun initKoin(platformContext: Any?) {
    // iOS doesn't need a Context; just start Koin with modules
    startKoin {
        modules(listOf(commonModule, iosModule))
    }
}
