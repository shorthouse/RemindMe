package dev.shorthouse.remindme.ui

import dev.shorthouse.remindme.data.protodatastore.ThemeStyle

data class MainActivityUiState(
    val themeStyle: ThemeStyle = ThemeStyle.AUTO,
    val isLoading: Boolean = false
)
