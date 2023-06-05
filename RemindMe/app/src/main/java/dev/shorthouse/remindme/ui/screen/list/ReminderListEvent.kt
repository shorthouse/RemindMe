package dev.shorthouse.remindme.ui.screen.list

import dev.shorthouse.remindme.data.protodatastore.ReminderFilter
import dev.shorthouse.remindme.data.protodatastore.ReminderSort
import dev.shorthouse.remindme.model.Reminder

sealed interface ReminderListEvent {
    data class UpdateFilter(val filter: ReminderFilter) : ReminderListEvent
    data class UpdateSortOrder(val sortOrder: ReminderSort) : ReminderListEvent
    data class CompleteReminder(val reminder: Reminder) : ReminderListEvent
    object ShowAddReminderSheet : ReminderListEvent
    object HideAddReminderSheet : ReminderListEvent
    object RemoveSnackbarMessage : ReminderListEvent
}
