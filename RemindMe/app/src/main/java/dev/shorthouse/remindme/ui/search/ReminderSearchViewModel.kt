package dev.shorthouse.remindme.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.domain.reminder.CompleteReminderUseCase
import dev.shorthouse.remindme.domain.reminder.GetRemindersFlowUseCase
import dev.shorthouse.remindme.model.Reminder
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

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
        val remindersFlow = getRemindersFlowUseCase()
        val searchQueryFlow = _searchQuery.asStateFlow()

        combine(remindersFlow, searchQueryFlow) { reminders, searchQuery ->
            val searchReminders = when {
                searchQuery.isEmpty() -> emptyList()
                else -> reminders.filter { it.name.contains(searchQuery, ignoreCase = true) }
            }

            _uiState.value.copy(
                searchReminders = searchReminders,
                isLoading = false
            )
        }
            .onEach { _uiState.value = it }
            .launchIn(viewModelScope)
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        _searchQuery.value = query
    }

    fun completeReminder(reminder: Reminder) {
        completeReminderUseCase(reminder)
    }
}
