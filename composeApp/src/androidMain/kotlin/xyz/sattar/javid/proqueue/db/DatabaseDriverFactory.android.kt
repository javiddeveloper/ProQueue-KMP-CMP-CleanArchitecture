package xyz.sattar.javid.proqueue.db

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

actual class DatabaseDriverFactory actual constructor(platformContext: Any?) {
    private val context: Context?

    init {
        // expect a real Android Context passed from Android code
        context = platformContext as? Context
            ?: throw IllegalArgumentException("Android Context must be provided to DatabaseDriverFactory on Android")
    }

    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(AppDatabase.Schema, context!!, "app.db")
    }
}
