package dev.shorthouse.remindme.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.shorthouse.remindme.model.Reminder

@Database(version = 1, exportSchema = false, entities = [Reminder::class])
abstract class ReminderDatabase : RoomDatabase() {
    abstract fun reminderDao(): ReminderDao

    companion object {
        @Volatile
        private var INSTANCE: ReminderDatabase? = null

        fun getDatabase(context: Context): ReminderDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    ReminderDatabase::class.java,
                    "reminder_database"
                )
                    .build()
                INSTANCE = instance

                instance
            }
        }
    }
}