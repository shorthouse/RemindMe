package dev.shorthouse.habitbuilder.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.shorthouse.habitbuilder.model.Reminder

@Database(version = 1, exportSchema = false, entities = arrayOf(Reminder::class))
abstract class ReminderDatabase : RoomDatabase() {
    abstract fun habitDao(): ReminderDao

    companion object {
        @Volatile
        private var INSTANCE: ReminderDatabase? = null

        fun getDatabase(context: Context): ReminderDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    ReminderDatabase::class.java,
                    "reminder_database")
                    .build()
                INSTANCE = instance

                instance
            }
        }
    }
}