package dev.shorthouse.remindme.data.source.local

import dev.shorthouse.remindme.data.ReminderDataSource
import dev.shorthouse.remindme.model.Reminder
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class ReminderRepository @Inject constructor(
    private val reminderLocalDataSource: ReminderDataSource
) {
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

    fun getOverdueReminders(now: ZonedDateTime): Flow<List<Reminder>> {
        return reminderLocalDataSource.getOverdueReminders(now)
    }

    fun getUpcomingReminders(now: ZonedDateTime): Flow<List<Reminder>> {
        return reminderLocalDataSource.getUpcomingReminders(now)
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