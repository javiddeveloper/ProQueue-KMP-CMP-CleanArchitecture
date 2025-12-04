package xyz.sattar.javid.proqueue.feature.settings

sealed interface SettingsEvent {
    data object NavigateToAbout : SettingsEvent
    data object NavigateToBusinessSelection : SettingsEvent
    data object BusinessDeleted : SettingsEvent
}
