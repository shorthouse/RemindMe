package dev.shorthouse.remindme.data

import dev.shorthouse.remindme.model.Reminder
import java.time.ZonedDateTime
import kotlinx.coroutines.flow.Flow

interface ReminderDataSource {
    fun getReminder(id: Long): Flow<Reminder>

    suspend fun getReminderOneShot(id: Long): Reminder

    fun getReminders(): Flow<List<Reminder>>

    suspend fun getRemindersOneShot(): List<Reminder>

    fun getOverdueReminders(now: ZonedDateTime): Flow<List<Reminder>>

    fun getUpcomingReminders(now: ZonedDateTime): Flow<List<Reminder>>

    fun getCompletedReminders(): Flow<List<Reminder>>

    fun completeReminder(id: Long)

    fun insertReminder(reminder: Reminder): Long

    fun updateReminder(reminder: Reminder)

    fun deleteReminder(reminder: Reminder)
}
