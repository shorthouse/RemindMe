package dev.shorthouse.remindme.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.model.Reminder
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(
    private val repository: ReminderRepository
) : ViewModel() {
    fun getReminder(reminderId: Long): LiveData<Reminder> {
        return repository.getReminder(reminderId).asLiveData()
    }
}
