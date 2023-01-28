package dev.shorthouse.remindme.compose.state

import androidx.compose.ui.graphics.vector.ImageVector

data class ReminderDetailItem(
    val icon: ImageVector,
    val label: String,
    val contentDescription: String
)
