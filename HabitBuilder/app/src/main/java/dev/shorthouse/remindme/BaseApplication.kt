package dev.shorthouse.remindme

import android.app.Application
import dev.shorthouse.remindme.data.ReminderDatabase

class BaseApplication : Application() {
    val database: ReminderDatabase by lazy {
        ReminderDatabase.getDatabase(this)
    }
}