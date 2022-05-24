package dev.shorthouse.remindme.viewmodel

import androidx.lifecycle.*
import dev.shorthouse.remindme.BaseApplication
import dev.shorthouse.remindme.data.ReminderDatabase
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.model.Reminder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderDetailsViewModel(
    application: BaseApplication,
) : ViewModel() {
    private val repository = ReminderRepository(
        ReminderDatabase.getDatabase(application).reminderDao()
    )

    fun getReminder(id: Long): LiveData<Reminder> {
        return repository.getReminder(id).asLiveData()
    }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteReminder(reminder)
        }
    }
}

class ReminderDetailsViewModelFactory(
    private val application: BaseApplication,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReminderDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReminderDetailsViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}