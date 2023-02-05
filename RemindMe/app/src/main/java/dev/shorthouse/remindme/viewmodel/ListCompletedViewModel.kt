package dev.shorthouse.remindme.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.util.enums.ReminderSortOrder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListCompletedViewModel @Inject constructor(
    private val repository: ReminderRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    var selectedReminderState by mutableStateOf(ReminderState())

    fun getCompletedReminderStates(reminderSortOrder: ReminderSortOrder): LiveData<List<ReminderState>> {
        return repository.getCompletedReminders()
            .map { sortReminders(it, reminderSortOrder) }
            .map { it.map { reminder -> ReminderState(reminder) } }
            .asLiveData()
    }

    fun deleteSelectedReminder() {
        val reminder = selectedReminderState.toReminder()

        viewModelScope.launch(ioDispatcher) {
            repository.deleteReminder(reminder)
        }
    }

    fun deleteCompletedReminders() {
        viewModelScope.launch(ioDispatcher) {
            repository.deleteCompletedReminders()
        }
    }

    private fun sortReminders(reminders: List<Reminder>, reminderSortOrder: ReminderSortOrder): List<Reminder> {
        return when (reminderSortOrder) {
            ReminderSortOrder.EARLIEST_DATE_FIRST -> reminders.sortedBy { it.startDateTime }
            else -> reminders.sortedByDescending { it.startDateTime }
        }
    }
}
