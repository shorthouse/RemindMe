package dev.shorthouse.remindme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.data.ReminderRepository
import javax.inject.Inject

@HiltViewModel
class ReminderListViewModel @Inject constructor(
    private val repository: ReminderRepository
) : ViewModel() {
    val allReminders = repository.getNotCompletedReminders().asLiveData()
}
