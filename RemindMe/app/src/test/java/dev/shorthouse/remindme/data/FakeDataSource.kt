package dev.shorthouse.remindme.data

import dev.shorthouse.remindme.model.Reminder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.ZonedDateTime

class FakeDataSource(private var reminders: MutableList<Reminder> = mutableListOf()) : ReminderDataSource {
    override fun getReminders(): Flow<List<Reminder>> {
        return flowOf(reminders.toList())
    }

    override fun getReminder(id: Long): Flow<Reminder> {
        return flowOf(reminders.first { it.id == id })
    }

    override fun getActiveNotCompletedReminders(nowDateTime: ZonedDateTime): Flow<List<Reminder>> {
        return flowOf(
            reminders.filter { it.startDateTime.isBefore(nowDateTime) || it.startDateTime.isEqual(nowDateTime) }
        )
    }

    override fun getNotCompletedReminders(): Flow<List<Reminder>> {
        return flowOf(reminders.filter { !it.isComplete })
    }

    override fun completeReminder(id: Long) {
        val reminderToCompleteIndex = reminders.indexOfFirst { it.id == id }
        val reminderToComplete = reminders[reminderToCompleteIndex]

        val completedReminder = Reminder(
            reminderToComplete.id,
            reminderToComplete.name,
            reminderToComplete.startDateTime,
            reminderToComplete.repeatInterval,
            reminderToComplete.notes,
            isComplete = true,
            reminderToComplete.isNotificationSent,
        )

        reminders[reminderToCompleteIndex] = completedReminder
    }

    override fun insertReminder(reminder: Reminder): Long {
        reminders.add(reminder)
        return reminders.last().id
    }

    override fun updateReminder(reminder: Reminder) {
        val reminderToUpdateIndex = reminders.indexOfFirst { it.id == reminder.id }
        reminders[reminderToUpdateIndex] = reminder
    }

    override fun deleteReminder(reminder: Reminder) {
        val reminderToDeleteIndex = reminders.indexOfFirst { it.id == reminder.id }
        reminders.removeAt(reminderToDeleteIndex)
    }
}
