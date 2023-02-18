package dev.shorthouse.remindme.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.data.protodatastore.ReminderSortOrder
import dev.shorthouse.remindme.data.protodatastore.UserPreferences
import dev.shorthouse.remindme.data.protodatastore.UserPreferencesRepository
import dev.shorthouse.remindme.model.Reminder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ListSearchViewModel @Inject constructor(
    repository: ReminderRepository,
    userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val reminders = repository.getReminders()
    private val userPreferences = userPreferencesRepository.userPreferencesFlow

    private val sortedReminderStates = combine(
        reminders,
        userPreferences
    ) { reminders: List<Reminder>, userPreferences: UserPreferences ->
        return@combine when (userPreferences.reminderSortOrder) {
            ReminderSortOrder.BY_EARLIEST_DATE_FIRST -> reminders.sortedBy { it.startDateTime }
            else -> reminders.sortedByDescending { it.startDateTime }
        }
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
}
