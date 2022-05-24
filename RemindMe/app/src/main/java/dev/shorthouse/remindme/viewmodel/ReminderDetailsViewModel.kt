package dev.shorthouse.remindme.viewmodel

import androidx.lifecycle.*
import dev.shorthouse.remindme.BaseApplication
import dev.shorthouse.remindme.data.ReminderDatabase
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.utilities.DATE_PATTERN
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

class ReminderDetailsViewModel(
    application: BaseApplication
) : AndroidViewModel(application) {
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

    fun getFormattedStartDate(reminder: Reminder?): String {
        if (reminder == null) return ""
        return reminder.startDateTime
            .toLocalDate()
            .format(DateTimeFormatter.ofPattern(DATE_PATTERN))
            .toString()
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