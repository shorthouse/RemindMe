package dev.shorthouse.remindme.ui.settings

import dev.shorthouse.remindme.data.protodatastore.Theme

data class SettingsUiState(
    val theme: Theme = Theme.AUTO,
    val isNotificationOnByDefault: Boolean = false,
    val isLoading: Boolean = false
)
