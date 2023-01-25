package dev.shorthouse.remindme.data

import dev.shorthouse.remindme.model.Reminder
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderRepository @Inject constructor(private val reminderLocalDataSource: ReminderDataSource) {
    fun getReminders(): Flow<List<Reminder>> {
        return reminderLocalDataSource.getReminders()
    }

    fun getReminder(id: Long): Flow<Reminder> {
        return reminderLocalDataSource.getReminder(id)
    }

    fun getOverdueReminders(): Flow<List<Reminder>> {
        return reminderLocalDataSource.getOverdueReminders(ZonedDateTime.now())
    }

    fun getScheduledReminders(): Flow<List<Reminder>> {
        return reminderLocalDataSource.getScheduledReminders()
    }

    fun getCompletedReminders(): Flow<List<Reminder>> {
        return reminderLocalDataSource.getCompletedReminders()
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
