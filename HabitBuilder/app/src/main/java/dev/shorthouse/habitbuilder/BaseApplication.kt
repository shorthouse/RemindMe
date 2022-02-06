package dev.shorthouse.habitbuilder

import android.app.Application
import dev.shorthouse.habitbuilder.data.HabitDatabase

class BaseApplication: Application() {
    val database: HabitDatabase by lazy {
        HabitDatabase.getDatabase(this)
    }
}