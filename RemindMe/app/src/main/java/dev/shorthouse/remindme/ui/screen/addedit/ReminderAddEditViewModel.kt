package dev.shorthouse.remindme.ui.screen.addedit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.data.Result
import dev.shorthouse.remindme.domain.reminder.AddReminderUseCase
import dev.shorthouse.remindme.domain.reminder.CompleteOnetimeReminderUseCase
import dev.shorthouse.remindme.domain.reminder.CompleteRepeatReminderSeriesUseCase
import dev.shorthouse.remindme.domain.reminder.DeleteReminderUseCase
import dev.shorthouse.remindme.domain.reminder.GetReminderUseCase
import dev.shorthouse.remindme.domain.reminder.UpdateReminderUseCase
import dev.shorthouse.remindme.domain.userpreferences.GetUserPreferencesFlowUseCase
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.model.RepeatInterval
import dev.shorthouse.remindme.ui.screen.navArgs
import dev.shorthouse.remindme.util.SnackbarMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class ReminderAddEditViewModel @Inject constructor(
    private val getUserPreferencesFlowUseCase: GetUserPreferencesFlowUseCase,
    private val getReminderUseCase: GetReminderUseCase,
    private val addReminderUseCase: AddReminderUseCase,
    private val updateReminderUseCase: UpdateReminderUseCase,
    private val deleteReminderUseCase: DeleteReminderUseCase,
    private val completeOnetimeReminderUseCase: CompleteOnetimeReminderUseCase,
    private val completeRepeatReminderSeriesUseCase: CompleteRepeatReminderSeriesUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReminderAddEditUiState())

    val uiState = _uiState.asStateFlow()

    init {
        initialiseUiState()
    }

    private fun initialiseUiState() {
        _uiState.update { it.copy(isLoading = true) }

        val navArgs = savedStateHandle.navArgs<ReminderAddEditScreenNavArgs>()

        if (navArgs.reminderId == null) {
            setAddReminder()
        } else {
            setEditReminder(navArgs.reminderId)
        }
    }

    fun handleEvent(event: ReminderAddEditEvent) {
        when (event) {
            is ReminderAddEditEvent.CompleteReminder -> completeReminder(event.reminder)
            is ReminderAddEditEvent.DeleteReminder -> deleteReminder(event.reminder)
            is ReminderAddEditEvent.SaveReminder -> saveReminder(event.reminder)
            is ReminderAddEditEvent.ResetState -> resetState()
            is ReminderAddEditEvent.UpdateName -> updateName(event.name)
            is ReminderAddEditEvent.UpdateDate -> updateDate(event.date)
            is ReminderAddEditEvent.UpdateTime -> updateTime(event.time)
            is ReminderAddEditEvent.UpdateNotes -> updateNotes(event.notes)
            is ReminderAddEditEvent.UpdateNotification -> updateNotification(event.notification)
            is ReminderAddEditEvent.UpdateRepeatInterval -> updateRepeatInterval(
                event.repeatInterval
            )

            is ReminderAddEditEvent.RemoveSnackbarMessage -> removeSnackbarMessage()
        }
    }

    private fun setAddReminder() {
        getUserPreferencesFlowUseCase()
            .onEach { userPreferences ->
                val initialReminder = _uiState.value.initialReminder.copy(
                    isNotificationSent = userPreferences.isNotificationDefaultOn
                )

                _uiState.update {
                    it.copy(
                        reminder = initialReminder,
                        initialReminder = initialReminder,
                        isLoading = false
                    )
                }
            }.launchIn(viewModelScope)
    }

    private fun setEditReminder(reminderId: Long) {
        viewModelScope.launch {
            val result = getReminderUseCase(reminderId = reminderId)

            _uiState.update {
                when (result) {
                    is Result.Success -> {
                        it.copy(
                            reminder = result.data,
                            initialReminder = result.data,
                            isLoading = false
                        )
                    }

                    is Result.Error -> {
                        it.copy(
                            snackbarMessage = SnackbarMessage(
                                messageId = R.string.error_loading_reminder_details
                            ),
                            isLoading = true
                        )
                    }
                }
            }
        }
    }

    private fun completeReminder(reminder: Reminder) {
        viewModelScope.launch {
            if (reminder.isRepeatReminder) {
                completeRepeatReminderSeriesUseCase(reminder)
            } else {
                completeOnetimeReminderUseCase(reminder)
            }
        }
    }

    private fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            deleteReminderUseCase(reminder)
        }
    }

    private fun saveReminder(reminder: Reminder) {
        viewModelScope.launch {
            if (reminder.id == 0L) {
                addReminderUseCase(reminder.validated())
            } else {
                updateReminderUseCase(reminder.validated())
            }
        }
    }

    private fun resetState() {
        _uiState.update { ReminderAddEditUiState() }
    }

    private fun updateReminder(updatedReminder: Reminder) {
        _uiState.update {
            it.copy(
                reminder = updatedReminder,
                isReminderValid = isReminderValid(updatedReminder)
            )
        }
    }

    private fun updateName(name: String) {
        if (name.length > 200) return

        val updatedReminder = _uiState.value.reminder.copy(name = name)
        updateReminder(updatedReminder)
    }

    private fun updateDate(date: LocalDate) {
        val updatedStartDateTime = date
            .atTime(_uiState.value.reminder.copy().startDateTime.toLocalTime())
            .atZone(ZoneId.systemDefault())

        val updatedReminder = _uiState.value.reminder.copy(startDateTime = updatedStartDateTime)
        updateReminder(updatedReminder)
    }

    private fun updateTime(time: LocalTime) {
        val updatedStartDateTime = time
            .atDate(_uiState.value.reminder.copy().startDateTime.toLocalDate())
            .atZone(ZoneId.systemDefault())

        val updatedReminder = _uiState.value.reminder.copy(startDateTime = updatedStartDateTime)
        updateReminder(updatedReminder)
    }

    private fun updateNotification(isNotificationSent: Boolean) {
        val updatedReminder = _uiState.value.reminder.copy(isNotificationSent = isNotificationSent)
        updateReminder(updatedReminder)
    }

    private fun updateRepeatInterval(repeatInterval: RepeatInterval?) {
        val updatedReminder = _uiState.value.reminder.copy(repeatInterval = repeatInterval)
        updateReminder(updatedReminder)
    }

    private fun updateNotes(notes: String) {
        if (notes.length > 2000) return

        val updatedReminder = _uiState.value.reminder.copy(notes = notes)
        updateReminder(updatedReminder)
    }

    private fun removeSnackbarMessage() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    private fun isReminderValid(reminder: Reminder): Boolean {
        return reminder.name.isNotBlank() &&
                reminder.startDateTime.isAfter(ZonedDateTime.now()) &&
                reminder.validated() != _uiState.value.initialReminder.validated()
    }
}
