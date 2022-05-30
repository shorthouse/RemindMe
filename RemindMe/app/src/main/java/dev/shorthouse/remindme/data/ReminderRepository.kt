package dev.shorthouse.remindme.data

import dev.shorthouse.remindme.model.Reminder
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderRepository @Inject constructor(
    private val reminderDao: ReminderDao
) {
    fun getReminders() = reminderDao.getReminders()

    fun getNonArchivedReminders() = reminderDao.getNonArchivedReminders()

    fun getActiveNonArchivedReminders(now: ZonedDateTime) =
        reminderDao.getActiveNonArchivedReminders(now)

    fun getReminder(reminderId: Long) = reminderDao.getReminder(reminderId)

    fun insertReminder(reminder: Reminder): Long {
        return reminderDao.insert(reminder)
    }

    fun updateReminder(reminder: Reminder) {
        reminderDao.update(reminder)
    }

    fun deleteReminder(reminder: Reminder) {
        reminderDao.delete(reminder)
    }
}
