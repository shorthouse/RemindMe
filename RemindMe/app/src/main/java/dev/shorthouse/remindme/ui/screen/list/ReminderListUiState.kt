package dev.shorthouse.remindme.ui.screen.list

import dev.shorthouse.remindme.data.protodatastore.ReminderFilter
import dev.shorthouse.remindme.data.protodatastore.ReminderSort
import dev.shorthouse.remindme.model.Reminder
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class ReminderListUiState(
    val reminders: ImmutableList<Reminder> = persistentListOf(),
    val reminderFilter: ReminderFilter = ReminderFilter.UPCOMING,
    val reminderSortOrder: ReminderSort = ReminderSort.BY_EARLIEST_DATE_FIRST,
    val isAddReminderSheetShown: Boolean = false,
    val isLoading: Boolean = false
)
