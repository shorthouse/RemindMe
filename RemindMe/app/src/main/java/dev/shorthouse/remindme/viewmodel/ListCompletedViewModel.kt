package dev.shorthouse.remindme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.util.enums.ReminderSortOrderOld
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListCompletedViewModel @Inject constructor(
    private val repository: ReminderRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    private val reminderListOrder = ReminderSortOrderOld.BY_EARLIEST_DATE_FIRST

    val completedReminderStates = repository.getCompletedReminders()
        .map { sortReminders(it, reminderListOrder) }
        .map { it.map { reminder -> ReminderState(reminder) } }

    fun deleteCompletedReminders() {
        viewModelScope.launch(ioDispatcher) {
            repository.deleteCompletedReminders()
        }
    }

    private fun sortReminders(reminders: List<Reminder>, reminderSortOrder: ReminderSortOrderOld): List<Reminder> {
        return when (reminderSortOrder) {
            ReminderSortOrderOld.BY_EARLIEST_DATE_FIRST -> reminders.sortedBy { it.startDateTime }
            else -> reminders.sortedByDescending { it.startDateTime }
        }
    }
}
