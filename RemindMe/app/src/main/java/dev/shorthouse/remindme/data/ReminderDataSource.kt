package dev.shorthouse.remindme.data

import dev.shorthouse.remindme.model.Reminder
import kotlinx.coroutines.flow.Flow

interface ReminderDataSource {
    fun getReminder(id: Long): Flow<Reminder>

    fun getReminders(): Flow<List<Reminder>>

    fun getActiveReminders(): Flow<List<Reminder>>

    fun getCompletedReminders(): Flow<List<Reminder>>

    fun completeReminder(id: Long)

    fun deleteCompletedReminders()

    fun insertReminder(reminder: Reminder): Long

    fun updateReminder(reminder: Reminder)

    fun deleteReminder(reminder: Reminder)
}
