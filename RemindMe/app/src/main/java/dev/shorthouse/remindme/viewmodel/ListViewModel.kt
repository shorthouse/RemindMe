package dev.shorthouse.remindme.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.domain.CompleteOnetimeReminderUseCase
import dev.shorthouse.remindme.domain.CompleteRepeatReminderOccurrenceUseCase
import dev.shorthouse.remindme.domain.CompleteRepeatReminderSeriesUseCase
import dev.shorthouse.remindme.domain.DeleteReminderUseCase
import dev.shorthouse.remindme.enums.ReminderAction
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val completeOnetimeReminderUseCase: CompleteOnetimeReminderUseCase,
    private val completeRepeatReminderOccurrenceUseCase: CompleteRepeatReminderOccurrenceUseCase,
    private val completeRepeatReminderSeriesUseCase: CompleteRepeatReminderSeriesUseCase,
    private val deleteReminderUseCase: DeleteReminderUseCase
) : ViewModel() {
    fun processReminderAction(
        selectedReminderState: ReminderState,
        reminderAction: ReminderAction,
        onEdit: () -> Unit
    ) {
        val reminder = selectedReminderState.toReminder()

        when (reminderAction) {
            ReminderAction.EDIT -> onEdit()
            ReminderAction.COMPLETE_ONETIME -> completeOnetimeReminderUseCase(reminder)
            ReminderAction.COMPLETE_REPEAT_OCCURRENCE -> completeRepeatReminderOccurrenceUseCase(reminder)
            ReminderAction.COMPLETE_REPEAT_SERIES -> completeRepeatReminderSeriesUseCase(reminder)
            ReminderAction.DELETE -> deleteReminderUseCase(reminder)
        }
    }
}
