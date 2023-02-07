package dev.shorthouse.remindme.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.util.enums.ReminderListOrder
import kotlinx.coroutines.flow.map
import javax.inject.Inject


@HiltViewModel
class ListActiveViewModel @Inject constructor(
    repository: ReminderRepository
) : ViewModel() {
    private val reminderListOrder = ReminderListOrder.EARLIEST_DATE_FIRST

    val activeReminderStates = repository.getActiveReminders()
        .map { sortReminders(it, reminderListOrder) }
        .map { it.map { reminder -> ReminderState(reminder) } }

    private fun sortReminders(reminders: List<Reminder>, reminderSortOrder: ReminderListOrder): List<Reminder> {
        return when (reminderSortOrder) {
            ReminderListOrder.EARLIEST_DATE_FIRST -> reminders.sortedBy { it.startDateTime }
            else -> reminders.sortedByDescending { it.startDateTime }
        }
    }
}
