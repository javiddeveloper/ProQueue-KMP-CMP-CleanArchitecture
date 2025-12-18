package xyz.sattar.javid.proqueue.core.prefs

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import xyz.sattar.javid.proqueue.ProQueueApp
import androidx.core.content.edit

import xyz.sattar.javid.proqueue.core.state.AppThemeMode

actual object PreferencesManager {
    private val context: Context get() = ProQueueApp.appContext
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    }

    private const val KEY_THEME_MODE = "theme_mode"
    private const val KEY_DEFAULT_BUSINESS_ID = "default_business_id"
    private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
    private const val KEY_NOTIFICATION_REMINDER_MINUTES = "notification_reminder_minutes"

    private val _themeMode = MutableStateFlow(
        AppThemeMode.values()[prefs.getInt(KEY_THEME_MODE, 0).coerceIn(0, 2)]
    )
    private val _defaultBusinessId = MutableStateFlow<Long?>(
        if (prefs.contains(KEY_DEFAULT_BUSINESS_ID)) prefs.getLong(KEY_DEFAULT_BUSINESS_ID, 0L) else null
    )
    private val _notificationsEnabled = MutableStateFlow(
        prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, false)
    )
    private val _notificationReminderMinutes = MutableStateFlow(
        prefs.getInt(KEY_NOTIFICATION_REMINDER_MINUTES, 10) // Default 10 minutes
    )

    actual val themeMode: Flow<AppThemeMode> = _themeMode
    actual val defaultBusinessId: Flow<Long?> = _defaultBusinessId
    actual val notificationsEnabled: Flow<Boolean> = _notificationsEnabled
    actual val notificationReminderMinutes: Flow<Int> = _notificationReminderMinutes

    actual suspend fun setThemeMode(mode: AppThemeMode) {
        prefs.edit(commit = true) { putInt(KEY_THEME_MODE, mode.ordinal) }
        _themeMode.value = mode
    }

    actual suspend fun setDefaultBusinessId(id: Long?) {
        prefs.edit(commit = true) {
            if (id == null) remove(KEY_DEFAULT_BUSINESS_ID) else putLong(
                KEY_DEFAULT_BUSINESS_ID,
                id
            )
        }
        _defaultBusinessId.value = id
    }

    actual suspend fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit(commit = true) { putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled) }
        _notificationsEnabled.value = enabled
    }

    actual suspend fun setNotificationReminderMinutes(minutes: Int) {
        prefs.edit(commit = true) { putInt(KEY_NOTIFICATION_REMINDER_MINUTES, minutes) }
        _notificationReminderMinutes.value = minutes
    }
}
