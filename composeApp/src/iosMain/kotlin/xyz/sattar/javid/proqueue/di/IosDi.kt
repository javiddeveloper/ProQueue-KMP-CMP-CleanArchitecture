package xyz.sattar.javid.proqueue.di

import org.koin.dsl.module
import xyz.sattar.javid.proqueue.db.DatabaseDriverFactory
import xyz.sattar.javid.proqueue.db.createDatabase

/**
 * iOS-specific Koin module. Creates DatabaseDriverFactory with null context
 * (Native driver will be used) and provides AppDatabase.
 */
val iosModule = module {
    single<DatabaseDriverFactory> { DatabaseDriverFactory(null) }
    single { createDatabase(get()) }
}
