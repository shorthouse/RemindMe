package dev.shorthouse.remindme.data

import androidx.room.*
import dev.shorthouse.remindme.model.Reminder
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminder")
    fun getReminders(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminder WHERE id = :id")
    fun getReminder(id: Long): Flow<Reminder>

    @Query("SELECT * FROM reminder WHERE startEpoch < :nowEpoch AND isArchived = 0")
    fun getActiveNonArchivedReminders(nowEpoch: Long): Flow<List<Reminder>>

    @Query("SELECT * FROM reminder WHERE isArchived = 0")
    fun getAllNonArchivedReminders(): Flow<List<Reminder>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(reminder: Reminder)

    @Update
    fun update(reminder: Reminder)

    @Delete
    fun delete(reminder: Reminder)
}