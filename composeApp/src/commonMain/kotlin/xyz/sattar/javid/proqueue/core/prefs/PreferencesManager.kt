package xyz.sattar.javid.proqueue.core.prefs

import kotlinx.coroutines.flow.Flow

import xyz.sattar.javid.proqueue.core.state.AppThemeMode

expect object PreferencesManager {
    val themeMode: Flow<AppThemeMode>
    val defaultBusinessId: Flow<Long?>
    suspend fun setThemeMode(mode: AppThemeMode)
    suspend fun setDefaultBusinessId(id: Long?)
}

