package dev.shorthouse.reminderbuilder.ui.viewmodel

import androidx.lifecycle.*
import dev.shorthouse.habitbuilder.data.ReminderDao
import dev.shorthouse.habitbuilder.model.Reminder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderViewModel(private val reminderDao: ReminderDao
) : ViewModel() {
    val reminders = reminderDao.getReminders().asLiveData()

    fun getReminder(id: Long): LiveData<Reminder> {
        return reminderDao.getReminder(id).asLiveData()
    }

    fun addReminder(
        name: String,
        notes: String,
    ) {
        val reminder = Reminder(
            name = name,
            notes = notes,
        )

        viewModelScope.launch(Dispatchers.IO) {
            reminderDao.insert(reminder)
        }
    }

    fun updateReminder(
        id: Long,
        name: String,
        notes: String,
    ) {
        val reminder = Reminder(
            id = id,
            name = name,
            notes = notes,
        )

        viewModelScope.launch(Dispatchers.IO) {
            reminderDao.update(reminder)
        }
    }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch(Dispatchers.IO) {
            reminderDao.delete(reminder)
        }
    }
}

class ReminderViewModelFactory(
    private val reminderDao: ReminderDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReminderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReminderViewModel(reminderDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}