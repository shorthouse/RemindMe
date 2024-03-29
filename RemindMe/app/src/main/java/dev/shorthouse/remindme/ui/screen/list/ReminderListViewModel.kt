package dev.shorthouse.remindme.ui.screen.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.data.protodatastore.ReminderFilter
import dev.shorthouse.remindme.data.protodatastore.ReminderSort
import dev.shorthouse.remindme.domain.reminder.CompleteReminderUseCase
import dev.shorthouse.remindme.domain.reminder.GetRemindersFlowUseCase
import dev.shorthouse.remindme.domain.userpreferences.GetUserPreferencesFlowUseCase
import dev.shorthouse.remindme.domain.userpreferences.UpdateReminderFilterUseCase
import dev.shorthouse.remindme.domain.userpreferences.UpdateReminderSortOrderUseCase
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.util.SnackbarMessage
import java.time.ZonedDateTime
import javax.inject.Inject
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ReminderListViewModel @Inject constructor(
    private val getRemindersFlowUseCase: GetRemindersFlowUseCase,
    private val getUserPreferencesFlowUseCase: GetUserPreferencesFlowUseCase,
    private val updateReminderFilterUseCase: UpdateReminderFilterUseCase,
    private val updateReminderSortOrderUseCase: UpdateReminderSortOrderUseCase,
    private val completeReminderUseCase: CompleteReminderUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReminderListUiState())

    val uiState = _uiState.asStateFlow()

    init {
        initialiseUiState()
    }

    fun initialiseUiState() {
        _uiState.update { it.copy(isLoading = true) }

        val remindersFlow = getRemindersFlowUseCase()
        val userPreferencesFlow = getUserPreferencesFlowUseCase()

        combine(remindersFlow, userPreferencesFlow) { reminders, userPreferences ->
            val now = ZonedDateTime.now()

            val filteredReminders = reminders.filter {
                when (userPreferences.reminderFilter) {
                    ReminderFilter.UPCOMING -> {
                        !it.startDateTime.isBefore(now) && !it.isCompleted
                    }

                    ReminderFilter.OVERDUE -> {
                        it.startDateTime.isBefore(now) && !it.isCompleted
                    }

                    ReminderFilter.COMPLETED -> {
                        it.isCompleted
                    }
                }
            }

            val sortedReminders = when (userPreferences.reminderSortOrder) {
                ReminderSort.BY_EARLIEST_DATE_FIRST -> {
                    filteredReminders.sortedBy { it.startDateTime }
                }

                ReminderSort.BY_LATEST_DATE_FIRST -> {
                    filteredReminders.sortedByDescending { it.startDateTime }
                }

                ReminderSort.BY_ALPHABETICAL_A_TO_Z -> {
                    filteredReminders.sortedBy { it.name }
                }

                ReminderSort.BY_ALPHABETICAL_Z_TO_A -> {
                    filteredReminders.sortedByDescending { it.name }
                }
            }

            _uiState.value.copy(
                reminders = sortedReminders.toImmutableList(),
                reminderFilter = userPreferences.reminderFilter,
                reminderSortOrder = userPreferences.reminderSortOrder,
                isLoading = false
            )
        }
            .onEach { _uiState.value = it }
            .launchIn(viewModelScope)
    }

    fun handleEvent(event: ReminderListEvent) {
        when (event) {
            is ReminderListEvent.UpdateFilter -> updateFilter(event.filter)
            is ReminderListEvent.UpdateSortOrder -> updateSortOrder(event.sortOrder)
            is ReminderListEvent.CompleteReminder -> completeReminder(event.reminder)
            is ReminderListEvent.ShowAddReminderSheet -> showAddReminderSheet()
            is ReminderListEvent.HideAddReminderSheet -> hideAddReminderSheet()
            is ReminderListEvent.RemoveSnackbarMessage -> removeSnackbarMessage()
        }
    }

    private fun updateFilter(filter: ReminderFilter) {
        viewModelScope.launch {
            updateReminderFilterUseCase(filter = filter)
        }
    }

    private fun updateSortOrder(sortOrder: ReminderSort) {
        viewModelScope.launch {
            updateReminderSortOrderUseCase(sortOrder = sortOrder)
        }
    }

    private fun completeReminder(reminder: Reminder) {
        viewModelScope.launch {
            completeReminderUseCase(reminder)
        }

        _uiState.update {
            it.copy(
                snackbarMessage = SnackbarMessage(
                    messageId = R.string.completed_reminder
                )
            )
        }
    }

    private fun showAddReminderSheet() {
        _uiState.update { it.copy(isAddReminderSheetShown = true) }
    }

    private fun hideAddReminderSheet() {
        _uiState.update { it.copy(isAddReminderSheetShown = false) }
    }

    private fun removeSnackbarMessage() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
