package dev.shorthouse.remindme.data

import androidx.room.*
import dev.shorthouse.remindme.model.Reminder
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminder")
    fun getReminders(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminder WHERE id = :id")
    fun getReminder(id: Long): Flow<Reminder>

    @Query("SELECT * FROM reminder WHERE startDateTime <= :nowDateTime AND isArchived = 0")
    fun getActiveNonArchivedReminders(nowDateTime: ZonedDateTime): Flow<List<Reminder>>

    @Query("SELECT * FROM reminder WHERE isArchived = 0")
    fun getNonArchivedReminders(): Flow<List<Reminder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(reminder: Reminder): Long

    @Update
    fun update(reminder: Reminder)

    @Delete
    fun delete(reminder: Reminder)
}