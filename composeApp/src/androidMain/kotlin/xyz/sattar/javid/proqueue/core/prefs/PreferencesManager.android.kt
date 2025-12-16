package xyz.sattar.javid.proqueue.core.prefs

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import xyz.sattar.javid.proqueue.ProQueueApp

actual object PreferencesManager {
    private val context: Context get() = ProQueueApp.appContext
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    }

    private const val KEY_DARK_THEME = "dark_theme"
    private const val KEY_DEFAULT_BUSINESS_ID = "default_business_id"

    private val _isDarkTheme = MutableStateFlow(prefs.getBoolean(KEY_DARK_THEME, false))
    private val _defaultBusinessId = MutableStateFlow<Long?>(
        if (prefs.contains(KEY_DEFAULT_BUSINESS_ID)) prefs.getLong(KEY_DEFAULT_BUSINESS_ID, 0L) else null
    )

    actual val isDarkTheme: Flow<Boolean> = _isDarkTheme
    actual val defaultBusinessId: Flow<Long?> = _defaultBusinessId

    actual suspend fun setDarkTheme(isDark: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_THEME, isDark).apply()
        _isDarkTheme.value = isDark
    }

    actual suspend fun setDefaultBusinessId(id: Long?) {
        prefs.edit().apply {
            if (id == null) remove(KEY_DEFAULT_BUSINESS_ID) else putLong(KEY_DEFAULT_BUSINESS_ID, id)
        }.apply()
        _defaultBusinessId.value = id
    }
}
