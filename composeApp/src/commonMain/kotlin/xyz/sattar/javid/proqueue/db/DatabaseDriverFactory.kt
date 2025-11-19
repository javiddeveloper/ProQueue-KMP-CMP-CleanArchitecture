package xyz.sattar.javid.proqueue.db

import com.squareup.sqldelight.db.SqlDriver

/**
 * Expect/actual factory to provide a platform-specific SqlDriver for SQLDelight.
 *
 * The constructor accepts a platform-specific context object as `Any?` so callers
 * can pass Android Context from Android code and `null` from other platforms.
 */
expect class DatabaseDriverFactory(platformContext: Any?) {
    fun createDriver(): SqlDriver
}
