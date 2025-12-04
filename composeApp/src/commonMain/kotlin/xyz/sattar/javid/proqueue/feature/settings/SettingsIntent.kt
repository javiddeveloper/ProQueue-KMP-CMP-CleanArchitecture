package xyz.sattar.javid.proqueue.feature.settings

sealed interface SettingsIntent {
    data object LoadSettings : SettingsIntent
    data object OnAboutClick : SettingsIntent
    data object OnLogoutClick : SettingsIntent
}
