package dev.shorthouse.remindme.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.utilities.enums.ReminderSortOrder
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ListAllViewModel @Inject constructor(
    private val repository: ReminderRepository
) : ViewModel() {
    fun getAllReminders(reminderSortOrder: ReminderSortOrder): LiveData<List<Reminder>> {
        return repository.getAllReminders().map { reminders ->
            when (reminderSortOrder) {
                ReminderSortOrder.EARLIEST_DATE_FIRST -> reminders.sortedBy { it.startDateTime }
                else -> reminders.sortedByDescending { it.startDateTime }
            }
        }.asLiveData()
    }
}
