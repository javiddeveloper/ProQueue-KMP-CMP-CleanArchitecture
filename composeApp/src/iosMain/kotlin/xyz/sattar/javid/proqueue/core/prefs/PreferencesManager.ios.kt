package xyz.sattar.javid.proqueue.core.prefs

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import platform.Foundation.*

actual object PreferencesManager {
    private val defaults = NSUserDefaults.standardUserDefaults

    private const val KEY_DARK_THEME = "dark_theme"
    private const val KEY_DEFAULT_BUSINESS_ID = "default_business_id"

    private val _isDarkTheme = MutableStateFlow(defaults.boolForKey(KEY_DARK_THEME))
    private fun readDefaultBusinessId(): Long? = if (defaults.objectForKey(KEY_DEFAULT_BUSINESS_ID) != null) {
        defaults.integerForKey(KEY_DEFAULT_BUSINESS_ID)
    } else null
    private val _defaultBusinessId = MutableStateFlow(readDefaultBusinessId())

    actual val isDarkTheme: Flow<Boolean> = _isDarkTheme
    actual val defaultBusinessId: Flow<Long?> = _defaultBusinessId

    actual suspend fun setDarkTheme(isDark: Boolean) {
        defaults.setBool(isDark, KEY_DARK_THEME)
        _isDarkTheme.value = isDark
    }

    actual suspend fun setDefaultBusinessId(id: Long?) {
        if (id == null) {
            defaults.removeObjectForKey(KEY_DEFAULT_BUSINESS_ID)
            _defaultBusinessId.value = null
        } else {
            defaults.setInteger(id, KEY_DEFAULT_BUSINESS_ID)
            _defaultBusinessId.value = id
        }
    }
}
