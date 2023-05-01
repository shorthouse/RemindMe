package dev.shorthouse.remindme.ui.settings

import dev.shorthouse.remindme.data.protodatastore.Theme

sealed interface SettingsEvent {
    data class SetTheme(val theme: Theme) : SettingsEvent
    data class SetNotification(val isNotificationOnByDefault: Boolean) : SettingsEvent
}
