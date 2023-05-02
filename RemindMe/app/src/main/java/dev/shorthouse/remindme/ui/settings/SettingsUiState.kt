package dev.shorthouse.remindme.ui.settings

import dev.shorthouse.remindme.data.protodatastore.ThemeStyle

data class SettingsUiState(
    val themeStyle: ThemeStyle = ThemeStyle.AUTO,
    val isNotificationOnByDefault: Boolean = false,
    val isLoading: Boolean = false
)
