package xyz.sattar.javid.proqueue.core.prefs

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import platform.Foundation.*

import xyz.sattar.javid.proqueue.core.state.AppThemeMode

actual object PreferencesManager {
    private val defaults = NSUserDefaults.standardUserDefaults

    private const val KEY_THEME_MODE = "theme_mode"
    private const val KEY_DEFAULT_BUSINESS_ID = "default_business_id"

    private val _themeMode = MutableStateFlow(
        AppThemeMode.values()[defaults.integerForKey(KEY_THEME_MODE).toInt().coerceIn(0, 2)]
    )
    private fun readDefaultBusinessId(): Long? = if (defaults.objectForKey(KEY_DEFAULT_BUSINESS_ID) != null) {
        defaults.integerForKey(KEY_DEFAULT_BUSINESS_ID)
    } else null
    private val _defaultBusinessId = MutableStateFlow(readDefaultBusinessId())

    actual val themeMode: Flow<AppThemeMode> = _themeMode
    actual val defaultBusinessId: Flow<Long?> = _defaultBusinessId

    actual suspend fun setThemeMode(mode: AppThemeMode) {
        defaults.setInteger(mode.ordinal.toLong(), KEY_THEME_MODE)
        _themeMode.value = mode
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
