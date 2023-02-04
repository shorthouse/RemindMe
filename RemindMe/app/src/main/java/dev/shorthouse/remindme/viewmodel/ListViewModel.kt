package dev.shorthouse.remindme.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.util.enums.ReminderList
import dev.shorthouse.remindme.util.enums.ReminderSortOrder
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val repository: ReminderRepository,
) : ViewModel() {
    fun getReminderStates(
        reminderList: ReminderList,
        reminderSortOrder: ReminderSortOrder
    ): LiveData<List<ReminderState>> {
        val reminders = when (reminderList) {
            ReminderList.SCHEDULED -> repository.getScheduledReminders()
            ReminderList.COMPLETED -> repository.getCompletedReminders()
        }

        return reminders
            .map { sortReminders(it, reminderSortOrder) }
            .map { it.map { reminder -> ReminderState(reminder) } }
            .asLiveData()
    }

    private fun sortReminders(reminders: List<Reminder>, reminderSortOrder: ReminderSortOrder): List<Reminder> {
        return when (reminderSortOrder) {
            ReminderSortOrder.EARLIEST_DATE_FIRST -> reminders.sortedBy { it.startDateTime }
            else -> reminders.sortedByDescending { it.startDateTime }
        }
    }
}
