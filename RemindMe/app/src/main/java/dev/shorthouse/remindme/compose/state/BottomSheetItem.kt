package dev.shorthouse.remindme.compose.state

import androidx.compose.ui.graphics.vector.ImageVector
import dev.shorthouse.remindme.enums.ReminderAction

data class BottomSheetActionItem(
    val icon: ImageVector,
    val label: String,
    val action: ReminderAction
)
