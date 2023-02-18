package dev.shorthouse.remindme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.data.protodatastore.ReminderSortOrder
import dev.shorthouse.remindme.data.protodatastore.UserPreferencesRepository
import dev.shorthouse.remindme.domain.DeleteCompletedRemindersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListCompletedViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val deleteCompletedRemindersUseCase: DeleteCompletedRemindersUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ListCompletedUiState())

    val uiState: StateFlow<ListCompletedUiState>
        get() = _uiState

    init {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val completedRemindersFlow = reminderRepository.getCompletedReminders()
            val userPreferencesFlow = userPreferencesRepository.userPreferencesFlow

            combine(completedRemindersFlow, userPreferencesFlow) { completedReminders, userPreferences ->
                val reminderSortOrder = userPreferences.reminderSortOrder

                val completedReminderStates = when (reminderSortOrder) {
                    ReminderSortOrder.BY_EARLIEST_DATE_FIRST -> completedReminders.sortedBy { it.startDateTime }
                    ReminderSortOrder.BY_LATEST_DATE_FIRST -> completedReminders.sortedByDescending { it.startDateTime }
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
