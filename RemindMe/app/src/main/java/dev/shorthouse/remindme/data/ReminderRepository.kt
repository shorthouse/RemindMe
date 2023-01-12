package dev.shorthouse.remindme.data

import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.utilities.enums.ReminderSortOrder
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

    fun getActiveReminders(): Flow<List<Reminder>> {
        return reminderLocalDataSource.getActiveReminders(ZonedDateTime.now())
    }

    fun getAllReminders(): Flow<List<Reminder>> {
        return reminderLocalDataSource.getAllReminders()
    }

    fun getAllRemindersFilteredSorted(searchFilter: String?, sortOrder: ReminderSortOrder): Flow<List<Reminder>> {
        val filter = searchFilter?.ifBlank { null }
        val sort = if (sortOrder == ReminderSortOrder.EARLIEST_DATE_FIRST) "ASC" else "DESC"

        return reminderLocalDataSource.getAllRemindersFilteredSorted(filter, sort)
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
