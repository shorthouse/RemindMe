package dev.shorthouse.remindme.ui.screen.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.domain.reminder.CompleteReminderUseCase
import dev.shorthouse.remindme.domain.reminder.GetRemindersFlowUseCase
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.util.SnackbarMessage
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReminderSearchViewModel @Inject constructor(
    private val getRemindersFlowUseCase: GetRemindersFlowUseCase,
    private val completeReminderUseCase: CompleteReminderUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReminderSearchUiState())

    val uiState = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        initialiseUiState()
    }

    private fun initialiseUiState() {
        _uiState.update { it.copy(isLoading = true) }

        val remindersFlow = getRemindersFlowUseCase()
        val searchQueryFlow = _searchQuery.asStateFlow()

        viewModelScope.launch {
            combine(remindersFlow, searchQueryFlow) { reminders, searchQuery ->
                val searchReminders = when {
                    searchQuery.isEmpty() -> emptyList()
                    else -> reminders.filter { it.name.contains(searchQuery, ignoreCase = true) }
                }

                _uiState.value.copy(
                    searchReminders = searchReminders.toImmutableList(),
                    isLoading = false
                )
            }
                .onEach { _uiState.value = it }
        }
    }

    fun handleEvent(event: ReminderSearchEvent) {
        when (event) {
            is ReminderSearchEvent.UpdateSearchQuery -> updateSearchQuery(event.query)
            is ReminderSearchEvent.RemoveSnackbarMessage -> removeSnackbarMessage()
        }
    }

    private fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        _searchQuery.value = query
    }

    fun completeReminder(reminder: Reminder) {
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

    private fun removeSnackbarMessage() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
