package dev.shorthouse.remindme.ui.details

import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.model.RepeatInterval
import java.time.LocalDate
import java.time.LocalTime

sealed interface ReminderDetailsEvent {
    data class CompleteReminder(val reminder: Reminder) : ReminderDetailsEvent
    data class DeleteReminder(val reminder: Reminder) : ReminderDetailsEvent
    data class SaveReminder(val reminder: Reminder) : ReminderDetailsEvent
    data class UpdateName(val name: String) : ReminderDetailsEvent
    data class UpdateDate(val date: LocalDate) : ReminderDetailsEvent
    data class UpdateTime(val time: LocalTime) : ReminderDetailsEvent
    data class UpdateNotification(val isNotificationSent: Boolean) : ReminderDetailsEvent
    data class UpdateRepeatInterval(val repeatInterval: RepeatInterval?) : ReminderDetailsEvent
    data class UpdateNotes(val notes: String) : ReminderDetailsEvent
}
