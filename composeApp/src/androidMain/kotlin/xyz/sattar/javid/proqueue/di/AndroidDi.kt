package xyz.sattar.javid.proqueue.di

import android.content.Context
import org.koin.dsl.module
import xyz.sattar.javid.proqueue.db.DatabaseDriverFactory
import xyz.sattar.javid.proqueue.db.createDatabase

/**
 * Android-specific DI module. Expects the Android `Context` to be provided
 * to Koin via `androidContext()` when starting Koin in Application.
 */
val androidModule = module {
    // Provide DatabaseDriverFactory using Android Context
    single { DatabaseDriverFactory(get<Context>()) }

    // Provide AppDatabase built from driver factory
    single { createDatabase(get()) }
}
