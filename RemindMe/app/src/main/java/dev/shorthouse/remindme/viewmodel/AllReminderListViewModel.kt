package dev.shorthouse.remindme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import dev.shorthouse.remindme.BaseApplication
import dev.shorthouse.remindme.data.ReminderDatabase
import dev.shorthouse.remindme.data.ReminderRepository

class AllReminderListViewModel(
    val application: BaseApplication
) : ViewModel() {
    private val repository = ReminderRepository(
        ReminderDatabase.getDatabase(application).reminderDao()
    )
    val reminders = repository.getNonArchivedReminders().asLiveData()
}

class AllReminderListViewModelFactory(
    private val application: BaseApplication
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AllReminderListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AllReminderListViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}