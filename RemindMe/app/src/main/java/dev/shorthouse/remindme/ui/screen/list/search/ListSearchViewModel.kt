package dev.shorthouse.remindme.ui.screen.list.search

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
class ListSearchViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ListSearchUiState())

    val uiState: StateFlow<ListSearchUiState>
        get() = _uiState

    private val _searchQuery = MutableStateFlow("")

    fun initialiseUiState() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch(ioDispatcher) {
            val remindersFlow = reminderRepository.getReminders()

            val userPreferencesFlow = userPreferencesRepository.userPreferencesFlow

            val searchQueryFlow = _searchQuery

            combine(remindersFlow, userPreferencesFlow, searchQueryFlow) { reminders, userPreferences, searchQuery ->
                val reminderSortOrder = userPreferences.reminderSortOrder

                val reminderStates = when (reminderSortOrder) {
                    ReminderSortOrder.BY_EARLIEST_DATE_FIRST -> reminders.sortedBy { it.startDateTime }
                    ReminderSortOrder.BY_LATEST_DATE_FIRST -> reminders.sortedByDescending { it.startDateTime }
                }
                    .map { ReminderState(it) }

                val searchReminderStates = if (searchQuery.isNotEmpty()) {
                    reminderStates.filter { it.name.contains(searchQuery, ignoreCase = true) }
                } else {
                    reminderStates
                }

                ListSearchUiState(
                    searchReminderStates = searchReminderStates,
                    searchQuery = searchQuery,
                    isLoading = false
                )
            }.collect { _uiState.value = it }
        }
    }

    fun setSearchQuery(searchQuery: String) {
        _searchQuery.value = searchQuery
    }
}

data class ListSearchUiState(
    val searchReminderStates: List<ReminderState> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false
)
