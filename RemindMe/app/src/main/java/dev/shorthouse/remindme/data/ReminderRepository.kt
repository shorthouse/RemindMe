package dev.shorthouse.remindme.data

import dev.shorthouse.remindme.model.Reminder
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderRepository @Inject constructor(private val reminderLocalDataSource: ReminderDataSource) {
    fun getReminder(id: Long): Flow<Reminder> {
        return reminderLocalDataSource.getReminder(id)
    }

    suspend fun getReminderOneShot(id: Long): Reminder {
        return reminderLocalDataSource.getReminderOneShot(id)
    }

    fun getReminders(): Flow<List<Reminder>> {
        return reminderLocalDataSource.getReminders()
    }

    suspend fun getRemindersOneShot(): List<Reminder> {
        return reminderLocalDataSource.getRemindersOneShot()
    }

    fun getActiveReminders(): Flow<List<Reminder>> {
        return reminderLocalDataSource.getActiveReminders()
    }

    fun getCompletedReminders(): Flow<List<Reminder>> {
        return reminderLocalDataSource.getCompletedReminders()
    }

    fun deleteCompletedReminders() {
        return reminderLocalDataSource.deleteCompletedReminders()
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

    fun completeReminder(id: Long) {
        return reminderLocalDataSource.completeReminder(id)
    }
}
