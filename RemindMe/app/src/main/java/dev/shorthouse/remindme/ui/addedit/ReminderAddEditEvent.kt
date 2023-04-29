package dev.shorthouse.remindme.ui.addedit

import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.model.RepeatInterval
import java.time.LocalDate
import java.time.LocalTime

sealed interface ReminderAddEditEvent {
    data class CompleteReminder(val reminder: Reminder) : ReminderAddEditEvent
    data class DeleteReminder(val reminder: Reminder) : ReminderAddEditEvent
    data class SaveReminder(val reminder: Reminder) : ReminderAddEditEvent
    data class UpdateName(val name: String) : ReminderAddEditEvent
    data class UpdateDate(val date: LocalDate) : ReminderAddEditEvent
    data class UpdateTime(val time: LocalTime) : ReminderAddEditEvent
    data class UpdateNotification(val isNotificationSent: Boolean) : ReminderAddEditEvent
    data class UpdateRepeatInterval(val repeatInterval: RepeatInterval?) : ReminderAddEditEvent
    data class UpdateNotes(val notes: String) : ReminderAddEditEvent
}
