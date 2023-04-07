package dev.shorthouse.remindme.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.data.protodatastore.ReminderFilter
import dev.shorthouse.remindme.data.protodatastore.ReminderSort
import dev.shorthouse.remindme.data.protodatastore.UserPreferencesRepository
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.domain.reminder.CompleteOnetimeReminderUseCase
import dev.shorthouse.remindme.domain.reminder.CompleteRepeatReminderOccurrenceUseCase
import dev.shorthouse.remindme.domain.reminder.CompleteRepeatReminderSeriesUseCase
import dev.shorthouse.remindme.domain.reminder.DeleteReminderUseCase
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.ui.util.enums.ReminderAction
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
    private val completeOnetimeReminderUseCase: CompleteOnetimeReminderUseCase,
    private val completeRepeatReminderOccurrenceUseCase: CompleteRepeatReminderOccurrenceUseCase,
    private val completeRepeatReminderSeriesUseCase: CompleteRepeatReminderSeriesUseCase,
    private val deleteReminderUseCase: DeleteReminderUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ListUiState())

    val uiState: StateFlow<ListUiState>
        get() = _uiState

    private val _searchQuery = MutableStateFlow("")
    private val _isSearchBarShown = MutableStateFlow(false)

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

    fun updateReminderSortOrder(reminderSortOrder: ReminderSort) {
        viewModelScope.launch(ioDispatcher) {
            userPreferencesRepository.updateReminderSortOrder(reminderSortOrder)
        }
    }

    fun updateReminderFilter(reminderFilter: ReminderFilter) {
        viewModelScope.launch(ioDispatcher) {
            userPreferencesRepository.updateReminderFilter(reminderFilter)
        }
    }

    fun updateBottomSheetReminder(bottomSheetReminder: Reminder) {
        _uiState.update { it.copy(bottomSheetReminder = bottomSheetReminder) }
    }

    fun updateIsBottomSheetShown(isBottomSheetShown: Boolean) {
        _uiState.update { it.copy(isBottomSheetShown = isBottomSheetShown) }
    }

    fun updateSearchQuery(searchQuery: String) {
        _searchQuery.value = searchQuery
        _uiState.update { it.copy(searchQuery = searchQuery) }
    }

    fun updateIsSearchBarShown(isSearchBarShown: Boolean) {
        _uiState.update { it.copy(isSearchBarShown = isSearchBarShown) }
    }

    fun processReminderAction(reminderAction: ReminderAction, reminder: Reminder) {
        when (reminderAction) {
            ReminderAction.COMPLETE_ONETIME -> {
                completeOnetimeReminderUseCase(reminder)
            }
            ReminderAction.COMPLETE_REPEAT_OCCURRENCE -> {
                completeRepeatReminderOccurrenceUseCase(reminder)
            }
            ReminderAction.COMPLETE_REPEAT_SERIES -> {
                completeRepeatReminderSeriesUseCase(reminder)
            }
            else -> {
                deleteReminderUseCase(reminder)
            }
        }
    }
}

data class ListUiState(
    val reminders: List<Reminder> = emptyList(),
    val reminderFilter: ReminderFilter = ReminderFilter.UPCOMING,
    val reminderSortOrder: ReminderSort = ReminderSort.BY_EARLIEST_DATE_FIRST,
    val searchQuery: String = "",
    val isSearchBarShown: Boolean = false,
    val bottomSheetReminder: Reminder = Reminder(),
    val isBottomSheetShown: Boolean = false,
    val isLoading: Boolean = true
)
