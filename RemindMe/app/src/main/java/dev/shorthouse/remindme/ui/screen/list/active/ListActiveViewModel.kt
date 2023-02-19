package dev.shorthouse.remindme.ui.screen.list.active

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.data.protodatastore.ReminderSortOrder
import dev.shorthouse.remindme.data.protodatastore.UserPreferencesRepository
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.ui.state.ReminderState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListActiveViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _uiState = MutableStateFlow(ListActiveUiState())

    val uiState: StateFlow<ListActiveUiState>
        get() = _uiState

    init {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val activeRemindersFlow = reminderRepository.getActiveReminders()

            val userPreferencesFlow = userPreferencesRepository.userPreferencesFlow

            combine(activeRemindersFlow, userPreferencesFlow) { activeReminders, userPreferences ->
                val reminderSortOrder = userPreferences.reminderSortOrder

                val activeReminderStates = when (reminderSortOrder) {
                    ReminderSortOrder.BY_EARLIEST_DATE_FIRST -> activeReminders.sortedBy { it.startDateTime }
                    ReminderSortOrder.BY_LATEST_DATE_FIRST -> activeReminders.sortedByDescending { it.startDateTime }
                }
                    .map { ReminderState(it) }

                ListActiveUiState(
                    activeReminderStates = activeReminderStates,
                    reminderSortOrder = reminderSortOrder,
                    isLoading = false
                )
            }.collect { _uiState.value = it }
        }
    }

    fun updateReminderSortOrder(reminderSortOrder: ReminderSortOrder) {
        viewModelScope.launch(ioDispatcher) {
            userPreferencesRepository.updateReminderSortOrder(reminderSortOrder)
        }
    }
}

data class ListActiveUiState(
    val activeReminderStates: List<ReminderState> = emptyList(),
    val reminderSortOrder: ReminderSortOrder = ReminderSortOrder.BY_EARLIEST_DATE_FIRST,
    val selectedReminderState: ReminderState = ReminderState(),
    val isLoading: Boolean = false
)
