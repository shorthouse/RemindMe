package dev.shorthouse.remindme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.data.protodatastore.ReminderSortOrder
import dev.shorthouse.remindme.data.protodatastore.UserPreferences
import dev.shorthouse.remindme.data.protodatastore.UserPreferencesRepository
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.model.Reminder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListActiveViewModel @Inject constructor(
    repository: ReminderRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val activeReminders = repository.getActiveReminders()
    val userPreferences = userPreferencesRepository.userPreferencesFlow

    val activeReminderStates = combine(
        activeReminders,
        userPreferences,
    ) { reminders: List<Reminder>, userPreferences: UserPreferences ->
        return@combine when (userPreferences.reminderSortOrder) {
            ReminderSortOrder.BY_EARLIEST_DATE_FIRST -> reminders.sortedBy { it.startDateTime }
            else -> reminders.sortedByDescending { it.startDateTime }
        }
            .map { ReminderState(it) }
    }

    fun updateReminderSortOrder(reminderSortOrder: ReminderSortOrder) {
        viewModelScope.launch(ioDispatcher) {
            userPreferencesRepository.updateReminderSortOrder(reminderSortOrder)
        }
    }
}
