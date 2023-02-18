package dev.shorthouse.remindme.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.data.protodatastore.UserPreferencesRepository
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.protodatastore.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ListSearchViewModel @Inject constructor(
    repository: ReminderRepository,
    userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val sortedReminderStates = combine(
        repository.getReminders(),
        userPreferencesRepository.userPreferencesFlow,
    ) { reminders: List<Reminder>, userPreferences: UserPreferences ->
        return@combine sortReminders(reminders, userPreferences.reminderSortOrder)
            .map { ReminderState(it) }
    }

    fun getSearchReminderStates(searchQuery: String): Flow<List<ReminderState>> {
        return sortedReminderStates
            .map { reminders ->
                if (searchQuery.isNotEmpty()) {
                    reminders.filter { reminder ->
                        reminder.name.contains(searchQuery, ignoreCase = true)
                    }
                } else {
                    reminders
                }
            }
    }

    private fun sortReminders(
        reminders: List<Reminder>,
        reminderSortOrder: UserPreferences.ReminderSortOrder
    ): List<Reminder> {
        return when (reminderSortOrder) {
            UserPreferences.ReminderSortOrder.BY_EARLIEST_DATE_FIRST -> reminders.sortedBy { it.startDateTime }
            else -> reminders.sortedByDescending { it.startDateTime }
        }
    }
}
