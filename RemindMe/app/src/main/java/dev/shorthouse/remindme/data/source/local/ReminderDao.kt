package dev.shorthouse.remindme.data.source.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.shorthouse.remindme.model.Reminder
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminder")
    fun getReminders(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminder WHERE id = :id")
    fun getReminder(id: Long): Flow<Reminder>

    @Query("SELECT * FROM reminder WHERE startDateTime <= :nowDateTime AND isComplete = 0")
    fun getActiveNonArchivedReminders(nowDateTime: ZonedDateTime): Flow<List<Reminder>>

    @Query("SELECT * FROM reminder WHERE isComplete = 0")
    fun getNonArchivedReminders(): Flow<List<Reminder>>

    @Query("UPDATE reminder SET isComplete = 1 WHERE id = :id")
    fun archiveReminder(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(reminder: Reminder): Long

    @Update
    fun update(reminder: Reminder)

    @Delete
    fun delete(reminder: Reminder)
}
