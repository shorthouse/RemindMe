package dev.shorthouse.remindme.data.source.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.shorthouse.remindme.model.Reminder
import java.time.ZonedDateTime
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

    @Query("SELECT * FROM reminder WHERE startDateTime <= :now AND isCompleted = 0")
    fun getOverdueReminders(now: ZonedDateTime): Flow<List<Reminder>>

    @Query("SELECT * FROM reminder WHERE startDateTime > :now AND isCompleted = 0")
    fun getUpcomingReminders(now: ZonedDateTime): Flow<List<Reminder>>

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
