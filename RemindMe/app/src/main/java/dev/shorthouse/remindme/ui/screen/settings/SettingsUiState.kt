package dev.shorthouse.remindme.ui.screen.settings

import dev.shorthouse.remindme.data.protodatastore.ThemeStyle

data class SettingsUiState(
    val themeStyle: ThemeStyle = ThemeStyle.AUTO,
    val isNotificationDefaultOn: Boolean = false,
    val isLoading: Boolean = false
)
