package dev.shorthouse.remindme.ui.details

import dev.shorthouse.remindme.model.Reminder

sealed interface ReminderDetailsEvent {
    data class CompleteReminder(val reminder: Reminder) : ReminderDetailsEvent
    data class DeleteReminder(val reminder: Reminder) : ReminderDetailsEvent
    data class SaveReminder(val reminder: Reminder) : ReminderDetailsEvent
}
