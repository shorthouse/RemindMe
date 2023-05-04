package dev.shorthouse.remindme.ui.search

import dev.shorthouse.remindme.model.Reminder

data class ReminderSearchUiState(
    val searchReminders: List<Reminder> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false
)
