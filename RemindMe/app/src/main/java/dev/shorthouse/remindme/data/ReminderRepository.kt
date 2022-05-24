package dev.shorthouse.remindme.data

import dev.shorthouse.remindme.model.Reminder
import java.time.ZonedDateTime

class ReminderRepository(
    private val reminderDao: ReminderDao
) {
    fun getAllReminders() = reminderDao.getReminders()

    fun getNonArchivedReminders() = reminderDao.getNonArchivedReminders()

    fun getActiveNonArchivedReminders(now: ZonedDateTime) =
        reminderDao.getActiveNonArchivedReminders(now)

    fun getReminder(reminderId: Long) = reminderDao.getReminder(reminderId)

    suspend fun insertReminder(reminder: Reminder): Long {
        return reminderDao.insert(reminder)
    }

    suspend fun updateReminder(reminder: Reminder) {
        reminderDao.update(reminder)
    }

    suspend fun deleteReminder(reminder: Reminder) {
        reminderDao.delete(reminder)
    }
}