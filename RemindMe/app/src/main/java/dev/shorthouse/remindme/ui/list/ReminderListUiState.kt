package dev.shorthouse.remindme.ui.list

import dev.shorthouse.remindme.data.protodatastore.ReminderFilter
import dev.shorthouse.remindme.data.protodatastore.ReminderSort
import dev.shorthouse.remindme.model.Reminder

data class ReminderListUiState(
    val reminders: List<Reminder> = emptyList(),
    val reminderFilter: ReminderFilter = ReminderFilter.UPCOMING,
    val reminderSortOrder: ReminderSort = ReminderSort.BY_EARLIEST_DATE_FIRST,
    val isAddReminderSheetShown: Boolean = false,
    val isLoading: Boolean = false
)
