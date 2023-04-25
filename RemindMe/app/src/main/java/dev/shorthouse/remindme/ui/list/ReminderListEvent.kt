package dev.shorthouse.remindme.ui.list

import dev.shorthouse.remindme.data.protodatastore.ReminderFilter
import dev.shorthouse.remindme.data.protodatastore.ReminderSort
import dev.shorthouse.remindme.model.Reminder

sealed interface ReminderListEvent {
    data class Filter(val filter: ReminderFilter) : ReminderListEvent
    data class Sort(val sortOrder: ReminderSort) : ReminderListEvent
    data class Search(val query: String) : ReminderListEvent
    data class CompleteReminder(val reminder: Reminder) : ReminderListEvent
    object ShowSearch : ReminderListEvent
    object HideSearch : ReminderListEvent
}
