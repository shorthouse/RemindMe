package dev.shorthouse.remindme.data.source.local

import dev.shorthouse.remindme.data.ReminderDataSource
import dev.shorthouse.remindme.model.Reminder
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReminderLocalDataSource @Inject constructor(private val reminderDao: ReminderDao) :
    ReminderDataSource {
    override fun getReminder(id: Long): Flow<Reminder> {
        return reminderDao.getReminder(id)
    }

    override suspend fun getReminderOneShot(id: Long): Reminder {
        return reminderDao.getReminderOneShot(id)
    }

    override fun getReminders(): Flow<List<Reminder>> {
        return reminderDao.getReminders()
    }

    override suspend fun getRemindersOneShot(): List<Reminder> {
        return reminderDao.getRemindersOneShot()
    }

    override fun completeReminder(id: Long) {
        return reminderDao.completeReminder(id)
    }

    override fun deleteCompletedReminders() {
        return reminderDao.deleteCompletedReminders()
    }

    override fun insertReminder(reminder: Reminder): Long {
        return reminderDao.insert(reminder)
    }

    override fun updateReminder(reminder: Reminder) {
        return reminderDao.update(reminder)
    }

    override fun deleteReminder(reminder: Reminder) {
        return reminderDao.delete(reminder)
    }
}
