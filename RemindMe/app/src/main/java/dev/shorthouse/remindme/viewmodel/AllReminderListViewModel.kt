package dev.shorthouse.remindme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.data.ReminderRepository
import javax.inject.Inject

@HiltViewModel
class AllReminderListViewModel @Inject constructor(
    repository: ReminderRepository,
) : ViewModel() {
    val reminders = repository.getNonArchivedReminders().asLiveData()
}
