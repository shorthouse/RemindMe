package dev.shorthouse.remindme.ui.screen.input

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.domain.reminder.AddReminderUseCase
import dev.shorthouse.remindme.domain.reminder.EditReminderUseCase
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.ui.state.ReminderState
import dev.shorthouse.remindme.ui.util.UiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class InputViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val addReminderUseCase: AddReminderUseCase,
    private val editReminderUseCase: EditReminderUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(InputUiState())

    val uiState: StateFlow<InputUiState>
        get() = _uiState

    fun setReminderState(reminderId: Long) {
        viewModelScope.launch {
            val reminder = reminderRepository.getReminder(reminderId)

            reminder.map {
                val reminderState = ReminderState(it)

                InputUiState(
                    reminderState = reminderState
                )
            }.collect { _uiState.value = it }
        }
    }

    fun isReminderValid(reminder: Reminder): Boolean {
        return reminder.name.isNotBlank() && reminder.startDateTime.isAfter(ZonedDateTime.now())
    }

    fun getErrorMessage(reminder: Reminder): UiText.StringResource {
        return when {
            reminder.name.isBlank() -> UiText.StringResource(R.string.error_name_empty)
            else -> UiText.StringResource(R.string.error_time_past)
        }
    }

    fun saveReminder(reminder: Reminder) {
        when (reminder.id) {
            0L -> addReminderUseCase(reminder)
            else -> editReminderUseCase(reminder)
        }
    }
}

data class InputUiState(
    val reminderState: ReminderState = ReminderState()
)
