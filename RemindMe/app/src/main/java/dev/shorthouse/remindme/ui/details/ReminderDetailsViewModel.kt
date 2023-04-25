package dev.shorthouse.remindme.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.data.source.local.ReminderRepository
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.domain.reminder.UpdateReminderUseCase
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.ui.state.ReminderState
import java.time.ZonedDateTime
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ReminderDetailsViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val updateReminderUseCase: UpdateReminderUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReminderDetailsUiState())

    val uiState: StateFlow<ReminderDetailsUiState>
        get() = _uiState

    val reminder = MutableStateFlow(
        ReminderState().toReminder()
    )

    fun setReminder(reminderId: Long) {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch(ioDispatcher) {
            reminderRepository.getReminder(reminderId)
                .map {
                    ReminderDetailsUiState(
                        initialReminder = it,
                        isLoading = false
                    )
                }.collect {
                    _uiState.value = it
                }
        }
    }

    fun saveReminder(reminderState: ReminderState) {
        val reminder = reminderState.toReminder()

        if (isReminderValid(reminder)) {
            updateReminderUseCase(reminder)
        }
    }

    private fun isReminderValid(reminder: Reminder): Boolean {
        return reminder.name.isNotBlank() && reminder.startDateTime.isAfter(ZonedDateTime.now())
    }
}
