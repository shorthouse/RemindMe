package dev.shorthouse.remindme.ui.addedit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.data.protodatastore.UserPreferencesRepository
import dev.shorthouse.remindme.data.source.local.ReminderRepository
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.domain.reminder.AddReminderUseCase
import dev.shorthouse.remindme.domain.reminder.CompleteOnetimeReminderUseCase
import dev.shorthouse.remindme.domain.reminder.CompleteRepeatReminderSeriesUseCase
import dev.shorthouse.remindme.domain.reminder.DeleteReminderUseCase
import dev.shorthouse.remindme.domain.reminder.UpdateReminderUseCase
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.model.RepeatInterval
import dev.shorthouse.remindme.ui.navArgs
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ReminderAddEditViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val addReminderUseCase: AddReminderUseCase,
    private val updateReminderUseCase: UpdateReminderUseCase,
    private val deleteReminderUseCase: DeleteReminderUseCase,
    private val completeOnetimeReminderUseCase: CompleteOnetimeReminderUseCase,
    private val completeRepeatReminderSeriesUseCase: CompleteRepeatReminderSeriesUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReminderAddEditUiState())

    val uiState: StateFlow<ReminderAddEditUiState>
        get() = _uiState

    init {
        val navArgs = savedStateHandle.navArgs<ReminderAddEditScreenNavArgs>()

        if (navArgs.reminderId == null) {
            setAddReminder()
        } else {
            setEditReminder(navArgs.reminderId)
        }
    }

    fun handleEvent(event: ReminderAddEditEvent) {
        when (event) {
            is ReminderAddEditEvent.CompleteReminder -> handleCompleteReminder(event.reminder)
            is ReminderAddEditEvent.DeleteReminder -> handleDeleteReminder(event.reminder)
            is ReminderAddEditEvent.SaveReminder -> handleSaveReminder(event.reminder)
            is ReminderAddEditEvent.ClearReminder -> handleClearReminder()
            is ReminderAddEditEvent.UpdateName -> handleUpdateName(event.name)
            is ReminderAddEditEvent.UpdateDate -> handleUpdateDate(event.date)
            is ReminderAddEditEvent.UpdateTime -> handleUpdateTime(event.time)
            is ReminderAddEditEvent.UpdateNotification ->
                handleUpdateNotification(event.isNotificationSent)
            is ReminderAddEditEvent.UpdateRepeatInterval ->
                handleUpdateRepeatInterval(event.repeatInterval)
            is ReminderAddEditEvent.UpdateNotes -> handleUpdateNotes(event.notes)
        }
    }

    fun isReminderValid(reminder: Reminder): Boolean {
        return reminder.name.isNotBlank() &&
                reminder.startDateTime.isAfter(ZonedDateTime.now()) &&
                reminder.validated() != _uiState.value.initialReminder.validated()
    }

    private fun setAddReminder() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch(ioDispatcher) {
            userPreferencesRepository.userPreferencesFlow.collectLatest { userPreferences ->
                val initialReminder = _uiState.value.initialReminder.copy(
                    isNotificationSent = userPreferences.isNotificationOnByDefault
                )

                _uiState.update {
                    it.copy(
                        reminder = initialReminder,
                        initialReminder = initialReminder,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun setEditReminder(reminderId: Long) {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch(ioDispatcher) {
            val initialReminder = reminderRepository.getReminderOneShot(reminderId)

            _uiState.update {
                it.copy(
                    reminder = initialReminder,
                    initialReminder = initialReminder,
                    isLoading = false
                )
            }
        }
    }

    private fun handleCompleteReminder(reminder: Reminder) {
        if (reminder.isRepeatReminder) {
            completeRepeatReminderSeriesUseCase(reminder)
        } else {
            completeOnetimeReminderUseCase(reminder)
        }
    }

    private fun handleDeleteReminder(reminder: Reminder) {
        deleteReminderUseCase(reminder)
    }

    private fun handleSaveReminder(reminder: Reminder) {
        if (reminder.id == 0L) {
            addReminderUseCase(reminder.validated())
        } else {
            updateReminderUseCase(reminder.validated())
        }
    }

    private fun handleClearReminder() {
        _uiState.update { it.copy(reminder = Reminder()) }
    }

    private fun updateReminder(updatedReminder: Reminder) {
        _uiState.update { it.copy(reminder = updatedReminder) }
    }

    private fun handleUpdateName(name: String) {
        if (name.length > 200) return

        val updatedReminder = _uiState.value.reminder.copy(name = name)
        updateReminder(updatedReminder)
    }

    private fun handleUpdateDate(date: LocalDate) {
        val updatedStartDateTime = date
            .atTime(_uiState.value.reminder.copy().startDateTime.toLocalTime())
            .atZone(ZoneId.systemDefault())

        val updatedReminder = _uiState.value.reminder.copy(startDateTime = updatedStartDateTime)
        updateReminder(updatedReminder)
    }

    private fun handleUpdateTime(time: LocalTime) {
        val updatedStartDateTime = time
            .atDate(_uiState.value.reminder.copy().startDateTime.toLocalDate())
            .atZone(ZoneId.systemDefault())

        val updatedReminder = _uiState.value.reminder.copy(startDateTime = updatedStartDateTime)
        updateReminder(updatedReminder)
    }

    private fun handleUpdateNotification(isNotificationSent: Boolean) {
        val updatedReminder = _uiState.value.reminder.copy(isNotificationSent = isNotificationSent)
        updateReminder(updatedReminder)
    }

    private fun handleUpdateRepeatInterval(repeatInterval: RepeatInterval?) {
        val updatedReminder = _uiState.value.reminder.copy(repeatInterval = repeatInterval)
        updateReminder(updatedReminder)
    }

    private fun handleUpdateNotes(notes: String) {
        if (notes.length > 2000) return

        val updatedReminder = _uiState.value.reminder.copy(notes = notes)
        updateReminder(updatedReminder)
    }
}
