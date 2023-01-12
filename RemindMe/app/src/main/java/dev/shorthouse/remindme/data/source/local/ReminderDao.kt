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

    @Query("SELECT * FROM reminder WHERE startDateTime <= :nowDateTime AND isComplete = 0")
    fun getActiveReminders(nowDateTime: ZonedDateTime): Flow<List<Reminder>>

    @Query("SELECT * FROM reminder WHERE isComplete = 0")
    fun getAllReminders(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminder WHERE name LIKE '%' || :searchFilter ORDER BY :orderBy")
    fun getAllRemindersFilteredSorted(searchFilter: String?, orderBy: String): Flow<List<Reminder>>

    @Query("UPDATE reminder SET isComplete = 1 WHERE id = :id")
    fun completeReminder(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(reminder: Reminder): Long

    @Update
    fun update(reminder: Reminder)

    @Delete
    fun delete(reminder: Reminder)
}
