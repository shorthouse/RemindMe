package dev.shorthouse.remindme.ui.details

import dev.shorthouse.remindme.model.Reminder

data class ReminderDetailsUiState(
    val reminder: Reminder = Reminder(),
    val initialReminder: Reminder = Reminder(),
    val isLoading: Boolean = false
)
