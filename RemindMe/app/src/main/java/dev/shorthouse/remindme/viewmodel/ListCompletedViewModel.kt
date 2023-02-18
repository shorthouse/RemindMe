package dev.shorthouse.remindme.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.data.protodatastore.ReminderSortOrder
import dev.shorthouse.remindme.data.protodatastore.UserPreferences
import dev.shorthouse.remindme.data.protodatastore.UserPreferencesRepository
import dev.shorthouse.remindme.domain.DeleteCompletedRemindersUseCase
import dev.shorthouse.remindme.model.Reminder
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class ListCompletedViewModel @Inject constructor(
    repository: ReminderRepository,
    userPreferencesRepository: UserPreferencesRepository,
    private val deleteCompletedRemindersUseCase: DeleteCompletedRemindersUseCase
) : ViewModel() {
    private val completedReminders = repository.getCompletedReminders()
    private val userPreferences = userPreferencesRepository.userPreferencesFlow

    val completedReminderStates = combine(
        completedReminders,
        userPreferences,
    ) { reminders: List<Reminder>, userPreferences: UserPreferences ->
        return@combine when (userPreferences.reminderSortOrder) {
            ReminderSortOrder.BY_EARLIEST_DATE_FIRST -> reminders.sortedBy { it.startDateTime }
            else -> reminders.sortedByDescending { it.startDateTime }
        }
            .map { ReminderState(it) }
    }

    fun deleteCompletedReminders() {
        deleteCompletedRemindersUseCase()
    }
}
