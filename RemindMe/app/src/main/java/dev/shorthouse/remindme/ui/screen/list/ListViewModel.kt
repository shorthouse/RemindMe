package dev.shorthouse.remindme.ui.screen.list

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.data.protodatastore.ReminderFilter
import dev.shorthouse.remindme.data.protodatastore.ReminderSort
import dev.shorthouse.remindme.data.protodatastore.UserPreferencesRepository
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.ui.state.ReminderState
import java.time.ZonedDateTime
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
        initialiseUiState()
    }

    @VisibleForTesting
    fun initialiseUiState() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch(ioDispatcher) {
            val remindersFlow = reminderRepository.getReminders()
            val userPreferencesFlow = userPreferencesRepository.userPreferencesFlow

            combine(
                remindersFlow,
                userPreferencesFlow
            ) { immutableReminders, userPreferences ->
                val reminders = immutableReminders.toMutableList()
                val reminderFilters = userPreferences.reminderFilters
                val now = ZonedDateTime.now()

                val unselectedFilters = ReminderFilter.values().toSet().minus(reminderFilters)

                unselectedFilters.forEach { reminderFilter ->
                    when (reminderFilter) {
                        ReminderFilter.OVERDUE -> {
                            reminders.removeAll {
                                it.startDateTime <= now && !it.isCompleted
                            }
                        }
                        ReminderFilter.SCHEDULED -> {
                            reminders.removeAll {
                                it.startDateTime > now && !it.isCompleted
                            }
                        }
                        ReminderFilter.COMPLETED -> {
                            reminders.removeAll {
                                it.isCompleted
                            }
                        }
                    }
                }

                val sortedReminders = when (userPreferences.reminderSortOrder) {
                    ReminderSort.BY_EARLIEST_DATE_FIRST -> {
                        reminders.sortedBy { it.startDateTime }
                    }
                    ReminderSort.BY_LATEST_DATE_FIRST -> {
                        reminders.sortedByDescending { it.startDateTime }
                    }
                }

                val reminderStates = sortedReminders.map { ReminderState(it) }

                ListActiveUiState(
                    reminderStates = reminderStates,
                    reminderFilters = reminderFilters,
                    reminderSortOrder = userPreferences.reminderSortOrder,
                    selectedReminderState = _uiState.value.selectedReminderState,
                    isLoading = false
                )
            }.collect { _uiState.value = it }
        }
    }

    fun updateReminderSortOrder(reminderSortOrder: ReminderSort) {
        viewModelScope.launch(ioDispatcher) {
            userPreferencesRepository.updateReminderSortOrder(reminderSortOrder)
        }
    }

    fun toggleReminderFilter(reminderFilter: ReminderFilter) {
        viewModelScope.launch(ioDispatcher) {
            userPreferencesRepository.toggleReminderFilter(reminderFilter)
        }
    }
}

data class ListActiveUiState(
    val reminderStates: List<ReminderState> = emptyList(),
    val reminderFilters: Set<ReminderFilter> = emptySet(),
    val reminderSortOrder: ReminderSort = ReminderSort.BY_EARLIEST_DATE_FIRST,
    val selectedReminderState: ReminderState = ReminderState(),
    val isLoading: Boolean = false
)
