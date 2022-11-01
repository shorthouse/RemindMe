package dev.shorthouse.remindme.data

import dev.shorthouse.remindme.model.Reminder
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface ReminderDataSource {
    fun getReminders(): Flow<List<Reminder>>

    fun getReminder(id: Long): Flow<Reminder>

    fun getActiveNotCompletedReminders(nowDateTime: ZonedDateTime): Flow<List<Reminder>>

    fun getNotCompletedReminders(): Flow<List<Reminder>>

    fun insertReminder(reminder: Reminder): Long

    fun updateReminder(reminder: Reminder)

    fun deleteReminder(reminder: Reminder)

    fun completeReminder(id: Long)
}
