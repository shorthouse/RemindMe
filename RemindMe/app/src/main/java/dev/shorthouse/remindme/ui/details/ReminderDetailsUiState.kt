package dev.shorthouse.remindme.ui.details

import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.ui.state.ReminderState

data class ReminderDetailsUiState(
    val initialReminder: Reminder = ReminderState().toReminder(),
    val isDropdownShown: Boolean = false,
    val isLoading: Boolean = false
)
