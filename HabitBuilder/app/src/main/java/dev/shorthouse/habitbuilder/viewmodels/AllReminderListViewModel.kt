package dev.shorthouse.habitbuilder.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import dev.shorthouse.habitbuilder.data.ReminderDao

class AllReminderListViewModel(
    private val reminderDao: ReminderDao
) : ViewModel() {
    val reminders = reminderDao.getAllNonArchivedReminders().asLiveData()
}

class AllReminderListViewModelFactory(
    private val reminderDao: ReminderDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AllReminderListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AllReminderListViewModel(reminderDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}