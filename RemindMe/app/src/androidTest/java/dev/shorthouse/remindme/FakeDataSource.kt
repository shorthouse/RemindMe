package dev.shorthouse.remindme

import dev.shorthouse.remindme.data.ReminderDataSource
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

    override fun getActiveNonArchivedReminders(nowDateTime: ZonedDateTime): Flow<List<Reminder>> {
        return flowOf(reminders.filter {
            it.startDateTime.isBefore(nowDateTime) || it.startDateTime.isEqual(nowDateTime)
        })
    }

    override fun getNonArchivedReminders(): Flow<List<Reminder>> {
        return flowOf(reminders.filter { !it.isArchived })
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

    override fun archiveReminder(id: Long) {
        val reminderToArchiveIndex = reminders.indexOfFirst { it.id == id }
        val unarchivedReminder = reminders[reminderToArchiveIndex]
        val archivedReminder = Reminder(
            unarchivedReminder.id,
            unarchivedReminder.name,
            unarchivedReminder.startDateTime,
            unarchivedReminder.repeatInterval,
            unarchivedReminder.notes,
            true,
            unarchivedReminder.isNotificationSent
        )

        reminders[reminderToArchiveIndex] = archivedReminder
    }
}