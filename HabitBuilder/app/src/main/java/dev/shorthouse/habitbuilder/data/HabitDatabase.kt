package dev.shorthouse.habitbuilder.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.shorthouse.habitbuilder.model.Habit

@Database(version = 1, exportSchema = false, entities = arrayOf(Habit::class))
abstract class HabitDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao

    companion object {
        @Volatile
        private var INSTANCE: HabitDatabase? = null

        fun getDatabase(context: Context): HabitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    HabitDatabase::class.java,
                    "habit_database")
                    .build()
                INSTANCE = instance

                instance
            }
        }
    }
}