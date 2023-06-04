package dev.shorthouse.remindme.ui.screen.search

import dev.shorthouse.remindme.model.Reminder
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class ReminderSearchUiState(
    val searchReminders: ImmutableList<Reminder> = persistentListOf(),
    val searchQuery: String = "",
    val isLoading: Boolean = false
)
