package dev.shorthouse.remindme.ui.addedit

import dev.shorthouse.remindme.model.Reminder

data class ReminderAddEditUiState(
    val reminder: Reminder = Reminder(),
    val initialReminder: Reminder = Reminder(),
    val isReminderValid: Boolean = false,
    val isLoading: Boolean = false
)
