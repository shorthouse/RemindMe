package dev.shorthouse.remindme.data

import dev.shorthouse.remindme.model.Reminder
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderRepository @Inject constructor(
    private val reminderLocalDataSource: ReminderDataSource
) {
    fun getReminders(): Flow<List<Reminder>> {
        return reminderLocalDataSource.getReminders()
    }

    fun getReminder(id: Long): Flow<Reminder> {
        return reminderLocalDataSource.getReminder(id)
    }

    fun getNonArchivedReminders(): Flow<List<Reminder>> {
        return reminderLocalDataSource.getNonArchivedReminders()
    }

    fun insertReminder(reminder: Reminder): Long {
        return reminderLocalDataSource.insertReminder(reminder)
    }

    fun updateReminder(reminder: Reminder) {
        return reminderLocalDataSource.updateReminder(reminder)
    }

    fun deleteReminder(reminder: Reminder) {
        return reminderLocalDataSource.deleteReminder(reminder)
    }

    fun archiveReminder(id: Long) {
        return reminderLocalDataSource.archiveReminder(id)
    }
}
