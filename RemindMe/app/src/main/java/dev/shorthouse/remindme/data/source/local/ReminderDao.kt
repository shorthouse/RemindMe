package dev.shorthouse.remindme.data.source.local

import androidx.room.*
import dev.shorthouse.remindme.model.Reminder
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminder WHERE id = :id")
    fun getReminder(id: Long): Flow<Reminder>

    @Query("SELECT * FROM reminder WHERE id = :id")
    suspend fun getReminderOneShot(id: Long): Reminder

    @Query("SELECT * FROM reminder")
    fun getReminders(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminder")
    suspend fun getRemindersOneShot(): List<Reminder>

    @Query("SELECT * FROM reminder WHERE isCompleted = 0")
    fun getActiveReminders(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminder WHERE isCompleted = 1")
    fun getCompletedReminders(): Flow<List<Reminder>>

    @Query("UPDATE reminder SET isCompleted = 1 WHERE id = :id")
    fun completeReminder(id: Long)

    @Query("DELETE FROM reminder WHERE isCompleted = 1")
    fun deleteCompletedReminders()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(reminder: Reminder): Long

    @Update
    fun update(reminder: Reminder)

    @Delete
    fun delete(reminder: Reminder)
}
