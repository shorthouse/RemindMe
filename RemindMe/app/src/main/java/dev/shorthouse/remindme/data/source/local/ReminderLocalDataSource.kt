package dev.shorthouse.remindme.data.source.local

import dev.shorthouse.remindme.data.ReminderDataSource
import dev.shorthouse.remindme.model.Reminder
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime
import javax.inject.Inject

class ReminderLocalDataSource @Inject constructor(
    private val reminderDao: ReminderDao,
) : ReminderDataSource {
    override fun getReminders(): Flow<List<Reminder>> {
        return reminderDao.getReminders()
    }

    override fun getReminder(id: Long): Flow<Reminder> {
        return reminderDao.getReminder(id)
    }

    override fun getActiveNonArchivedReminders(nowDateTime: ZonedDateTime): Flow<List<Reminder>> {
        return reminderDao.getActiveNonArchivedReminders(nowDateTime)
    }

    override fun getNonArchivedReminders(): Flow<List<Reminder>> {
        return reminderDao.getNonArchivedReminders()
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

    override fun archiveReminder(id: Long) {
        return reminderDao.archive(id)
    }
}