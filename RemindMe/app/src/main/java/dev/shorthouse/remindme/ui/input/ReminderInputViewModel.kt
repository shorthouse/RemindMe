package dev.shorthouse.remindme.ui.input

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.domain.reminder.AddReminderUseCase
import dev.shorthouse.remindme.domain.reminder.EditReminderUseCase
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.ui.state.ReminderState
import dev.shorthouse.remindme.ui.util.UiText
import java.time.ZonedDateTime
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ReminderInputViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val addReminderUseCase: AddReminderUseCase,
    private val editReminderUseCase: EditReminderUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(InputUiState())

    val uiState: StateFlow<InputUiState>
        get() = _uiState

    val reminder = MutableStateFlow(
        ReminderState().toReminder()
    )

    fun setReminder(reminderId: Long) {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch(ioDispatcher) {
            reminderRepository.getReminder(reminderId)
                .map {
                    InputUiState(
                        reminder = it,
                        isLoading = false
                    )
                }.collect {
                    _uiState.value = it
                }
        }
    }

    fun saveReminder(reminder: Reminder) {
        when (reminder.id) {
            0L -> addReminderUseCase(reminder)
            else -> editReminderUseCase(reminder)
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
}

data class InputUiState(
    val reminder: Reminder = ReminderState().toReminder(),
    val isLoading: Boolean = false
)
