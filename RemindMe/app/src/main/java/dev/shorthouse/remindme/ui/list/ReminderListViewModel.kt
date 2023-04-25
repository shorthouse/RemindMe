package dev.shorthouse.remindme.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.data.protodatastore.ReminderFilter
import dev.shorthouse.remindme.data.protodatastore.ReminderSort
import dev.shorthouse.remindme.data.protodatastore.UserPreferencesRepository
import dev.shorthouse.remindme.data.source.local.ReminderRepository
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.domain.reminder.CompleteReminderUseCase
import java.time.ZonedDateTime
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ReminderListViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val completeReminderUseCase: CompleteReminderUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReminderListUiState())

    val uiState: StateFlow<ReminderListUiState>
        get() = _uiState

    private val _searchQuery = MutableStateFlow("")
    private val _isSearchBarShown = MutableStateFlow(false)

    init {
        initialiseUiState()
    }

    fun initialiseUiState() {
        viewModelScope.launch(ioDispatcher) {
            val remindersFlow = reminderRepository.getReminders()
            val userPreferencesFlow = userPreferencesRepository.userPreferencesFlow
            val searchQueryFlow = _searchQuery
            val isSearchBarShownFlow = _isSearchBarShown

            combine(
                remindersFlow,
                userPreferencesFlow,
                searchQueryFlow,
                isSearchBarShownFlow
            ) { reminders, userPreferences, searchQuery, isSearchBarShown ->

                val searchReminders = when {
                    !isSearchBarShown -> reminders
                    searchQuery.isEmpty() -> emptyList()
                    else -> reminders.filter { it.name.contains(searchQuery, ignoreCase = true) }
                }

                val filteredReminders = if (isSearchBarShown) {
                    searchReminders
                } else {
                    val now = ZonedDateTime.now()

                    when (userPreferences.reminderFilter) {
                        ReminderFilter.OVERDUE -> {
                            reminders.filter {
                                it.startDateTime.isBefore(now) && !it.isCompleted
                            }
                        }
                        ReminderFilter.UPCOMING -> {
                            reminders.filter {
                                !it.startDateTime.isBefore(now) && !it.isCompleted
                            }
                        }
                        ReminderFilter.COMPLETED -> {
                            reminders.filter {
                                it.isCompleted
                            }
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
                    reminders = sortedReminders,
                    reminderFilter = userPreferences.reminderFilter,
                    reminderSortOrder = userPreferences.reminderSortOrder,
                    searchQuery = searchQuery,
                    isLoading = false
                )
            }.collect { _uiState.value = it }
        }
    }

    fun handleEvent(event: ReminderListEvent) {
        when (event) {
            is ReminderListEvent.Filter -> handleFilter(event)
            is ReminderListEvent.Sort -> handleSort(event)
            is ReminderListEvent.Search -> handleSearch(event)
            is ReminderListEvent.CompleteReminder -> handleCompleteReminder(event)
            ReminderListEvent.ShowSearch -> handleShowSearch()
            ReminderListEvent.HideSearch -> handleHideSearch()
        }
    }

    private fun handleFilter(event: ReminderListEvent.Filter) {
        viewModelScope.launch(ioDispatcher) {
            userPreferencesRepository.updateReminderFilter(event.filter)
        }
    }

    private fun handleSort(event: ReminderListEvent.Sort) {
        viewModelScope.launch(ioDispatcher) {
            userPreferencesRepository.updateReminderSortOrder(event.sortOrder)
        }
    }

    private fun handleSearch(event: ReminderListEvent.Search) {
        _searchQuery.value = event.query
        _uiState.update { it.copy(searchQuery = event.query) }
    }

    private fun handleCompleteReminder(event: ReminderListEvent.CompleteReminder) {
        completeReminderUseCase(event.reminder)
    }

    private fun handleShowSearch() {
        _uiState.update { it.copy(isSearchBarShown = true) }
    }

    private fun handleHideSearch() {
        _uiState.update {
            it.copy(isSearchBarShown = false, searchQuery = "")
        }
    }
}
