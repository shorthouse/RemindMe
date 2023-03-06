package dev.shorthouse.remindme.ui.screen.list.completed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.data.protodatastore.ReminderSortOrder
import dev.shorthouse.remindme.data.protodatastore.UserPreferencesRepository
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.domain.reminder.DeleteCompletedRemindersUseCase
import dev.shorthouse.remindme.ui.state.ReminderState
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ListCompletedViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val deleteCompletedRemindersUseCase: DeleteCompletedRemindersUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ListCompletedUiState())

    val uiState: StateFlow<ListCompletedUiState>
        get() = _uiState

    fun initialiseUiState() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch(ioDispatcher) {
            val completedRemindersFlow = reminderRepository.getCompletedReminders()

            val userPreferencesFlow = userPreferencesRepository.userPreferencesFlow

            combine(
                completedRemindersFlow,
                userPreferencesFlow
            ) { completedReminders, userPreferences ->
                val reminderSortOrder = userPreferences.reminderSortOrder

                val completedReminderStates = when (reminderSortOrder) {
                    ReminderSortOrder.BY_EARLIEST_DATE_FIRST -> {
                        completedReminders.sortedBy { it.startDateTime }
                    }
                    ReminderSortOrder.BY_LATEST_DATE_FIRST -> {
                        completedReminders.sortedByDescending { it.startDateTime }
                    }
                }
                    .map { ReminderState(it) }

                ListCompletedUiState(
                    completedReminderStates = completedReminderStates,
                    isLoading = false
                )
            }.collect { _uiState.value = it }
        }
    }

    fun deleteCompletedReminders() {
        deleteCompletedRemindersUseCase()
    }
}

data class ListCompletedUiState(
    val completedReminderStates: List<ReminderState> = emptyList(),
    val isLoading: Boolean = false
)
