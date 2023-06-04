package dev.shorthouse.remindme.ui.screen.addedit

import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.util.SnackbarMessage

data class ReminderAddEditUiState(
    val reminder: Reminder = Reminder(),
    val initialReminder: Reminder = Reminder(),
    val isReminderValid: Boolean = false,
    val snackbarMessage: SnackbarMessage? = null,
    val isLoading: Boolean = false
)
