package dev.shorthouse.remindme.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.data.source.local.ReminderRepository
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.domain.reminder.CompleteOnetimeReminderUseCase
import dev.shorthouse.remindme.domain.reminder.CompleteRepeatReminderSeriesUseCase
import dev.shorthouse.remindme.domain.reminder.DeleteReminderUseCase
import dev.shorthouse.remindme.domain.reminder.UpdateReminderUseCase
import dev.shorthouse.remindme.model.Reminder
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ReminderDetailsViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val updateReminderUseCase: UpdateReminderUseCase,
    private val deleteReminderUseCase: DeleteReminderUseCase,
    private val completeOnetimeReminderUseCase: CompleteOnetimeReminderUseCase,
    private val completeRepeatReminderSeriesUseCase: CompleteRepeatReminderSeriesUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReminderDetailsUiState())

    val uiState: StateFlow<ReminderDetailsUiState>
        get() = _uiState

    fun setReminder(reminderId: Long) {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch(ioDispatcher) {
            val initialReminder = reminderRepository.getReminderOneShot(reminderId)

            _uiState.update {
                it.copy(
                    initialReminder = initialReminder,
                    isLoading = false
                )
            }
        }
    }

    fun handleEvent(event: ReminderDetailsEvent) {
        when (event) {
            is ReminderDetailsEvent.CompleteReminder -> handleCompleteReminder(event.reminder)
            is ReminderDetailsEvent.DeleteReminder -> handleDeleteReminder(event.reminder)
            is ReminderDetailsEvent.SaveReminder -> handleSaveReminder(event.reminder)
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
        updateReminderUseCase(reminder)
    }
}
