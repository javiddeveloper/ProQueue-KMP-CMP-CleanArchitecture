package xyz.sattar.javid.proqueue.core.prefs

import kotlinx.coroutines.flow.Flow

expect object PreferencesManager {
    val isDarkTheme: Flow<Boolean>
    val defaultBusinessId: Flow<Long?>
    suspend fun setDarkTheme(isDark: Boolean)
    suspend fun setDefaultBusinessId(id: Long?)
}

