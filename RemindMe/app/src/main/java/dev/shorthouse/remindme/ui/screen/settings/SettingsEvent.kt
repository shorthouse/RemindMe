package dev.shorthouse.remindme.ui.screen.settings

import dev.shorthouse.remindme.data.protodatastore.ThemeStyle

sealed interface SettingsEvent {
    data class Theme(val theme: ThemeStyle) : SettingsEvent
    data class NotificationDefault(val isDefaultOn: Boolean) : SettingsEvent
}
