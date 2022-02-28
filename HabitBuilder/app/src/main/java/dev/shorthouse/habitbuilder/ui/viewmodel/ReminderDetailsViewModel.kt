package dev.shorthouse.habitbuilder.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.shorthouse.habitbuilder.data.ReminderDao

class ReminderDetailsViewModel(private val reminderDao: ReminderDao
) : ViewModel() {

}

class ReminderDetailsViewModelFactory(
    private val reminderDao: ReminderDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReminderDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReminderDetailsViewModel(reminderDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}