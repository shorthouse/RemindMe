package dev.shorthouse.remindme.compose.state

import androidx.compose.ui.graphics.painter.Painter

data class BottomSheetItem(
    val icon: Painter,
    val label: String
)
