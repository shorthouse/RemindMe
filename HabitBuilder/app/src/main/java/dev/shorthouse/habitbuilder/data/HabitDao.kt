package dev.shorthouse.habitbuilder.data

import androidx.room.*
import dev.shorthouse.habitbuilder.model.Habit
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Query("SELECT * FROM habit")
    fun getHabits(): Flow<List<Habit>>

    @Query("SELECT * FROM habit WHERE id = :id")
    fun getHabit(id: Long): Flow<Habit>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(habit: Habit)

    @Update
    fun update(habit: Habit)

    @Delete
    fun delete(habit: Habit)
}