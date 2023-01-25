package dev.shorthouse.remindme.data.source.local

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

    @Query("SELECT * FROM reminder WHERE startDateTime <= :nowDateTime AND isCompleted = 0")
    fun getOverdueReminders(nowDateTime: ZonedDateTime): Flow<List<Reminder>>

    @Query("SELECT * FROM reminder WHERE isCompleted = 0")
    fun getScheduledReminders(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminder WHERE isCompleted = 1")
    fun getCompletedReminders(): Flow<List<Reminder>>

    @Query("UPDATE reminder SET isCompleted = 1 WHERE id = :id")
    fun completeReminder(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(reminder: Reminder): Long

    @Update
    fun update(reminder: Reminder)

    @Delete
    fun delete(reminder: Reminder)
}
