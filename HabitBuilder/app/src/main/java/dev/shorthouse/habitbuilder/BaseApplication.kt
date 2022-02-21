package dev.shorthouse.habitbuilder

import android.app.Application
import dev.shorthouse.habitbuilder.data.ReminderDatabase

class BaseApplication: Application() {
    val database: ReminderDatabase by lazy {
        ReminderDatabase.getDatabase(this)
    }
}