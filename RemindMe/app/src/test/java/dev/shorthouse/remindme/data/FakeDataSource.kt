package dev.shorthouse.remindme.data

import dev.shorthouse.remindme.model.Reminder
import java.time.ZonedDateTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeDataSource(
    private var reminders: MutableList<Reminder> = mutableListOf()
) : ReminderDataSource {
    override fun getReminders(): Flow<List<Reminder>> {
        return flowOf(
            reminders
        )
    }

    override suspend fun getRemindersOneShot(): List<Reminder> {
        return reminders
    }

    override fun getReminder(id: Long): Flow<Reminder> {
        return flowOf(
            reminders.first { reminder ->
                reminder.id == id
            }
        )
    }

    override suspend fun getReminderOneShot(id: Long): Reminder {
        return reminders.first { reminder ->
            reminder.id == id
        }
    }

    override fun getOverdueReminders(now: ZonedDateTime): Flow<List<Reminder>> {
        return flowOf(
            reminders.filter {
                it.startDateTime.isBefore(now) && !it.isCompleted
            }
        )
    }

    override fun getUpcomingReminders(now: ZonedDateTime): Flow<List<Reminder>> {
        return flowOf(
            reminders.filter {
                !it.startDateTime.isBefore(now) && !it.isCompleted
            }
        )
    }

    override fun getCompletedReminders(): Flow<List<Reminder>> {
        return flowOf(
            reminders.filter {
                it.isCompleted
            }
        )
    }

    override fun insertReminder(reminder: Reminder): Long {
        reminders.add(reminder)

        return reminders.last().id
    }

    override fun updateReminder(reminder: Reminder) {
        val reminderToUpdateIndex = reminders.indexOfFirst {
            it.id == reminder.id
        }

        reminders[reminderToUpdateIndex] = reminder
    }

    override fun deleteReminder(reminder: Reminder) {
        val reminderToDeleteIndex = reminders.indexOfFirst {
            it.id == reminder.id
        }

        reminders.removeAt(reminderToDeleteIndex)
    }

    override fun completeReminder(id: Long) {
        val reminderToCompleteIndex = reminders.indexOfFirst { reminder ->
            reminder.id == id
        }

        val completedReminder = reminders[reminderToCompleteIndex].copy(isCompleted = true)

        reminders[reminderToCompleteIndex] = completedReminder
    }
}
