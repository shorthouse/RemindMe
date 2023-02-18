package dev.shorthouse.remindme.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.util.DAYS_IN_WEEK
import dev.shorthouse.remindme.util.NotificationScheduler
import dev.shorthouse.remindme.util.enums.ReminderAction
import dev.shorthouse.remindme.util.enums.ReminderSortOrderOld
import dev.shorthouse.remindme.util.floor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.time.Duration.Companion.days
import kotlin.time.DurationUnit
import kotlin.time.times
import kotlin.time.toDuration

@HiltViewModel
class ListViewModel @Inject constructor(
    private val repository: ReminderRepository,
    private val notificationScheduler: NotificationScheduler,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    val reminderListOrder = mutableStateOf(ReminderSortOrderOld.BY_EARLIEST_DATE_FIRST)

    fun processReminderAction(
        selectedReminderState: ReminderState,
        reminderAction: ReminderAction,
        onEdit: () -> Unit
    ) {
        when (reminderAction) {
            ReminderAction.EDIT -> onEdit()
            ReminderAction.COMPLETE_ONETIME -> completeOnetimeReminder(selectedReminderState)
            ReminderAction.COMPLETE_REPEAT_OCCURRENCE -> completeRepeatReminderOccurrence(selectedReminderState)
            ReminderAction.COMPLETE_REPEAT_SERIES -> completeRepeatReminderSeries(selectedReminderState)
            ReminderAction.DELETE -> deleteReminder(selectedReminderState)
        }
    }

    private fun completeOnetimeReminder(reminderState: ReminderState) {
        val reminder = reminderState.toReminder()

        removeReminderNotification(reminder)

        viewModelScope.launch(ioDispatcher) {
            repository.completeReminder(reminder.id)
        }
    }

    private fun completeRepeatReminderOccurrence(reminderState: ReminderState) {
        val reminder = reminderState.toReminder()

        val updatedReminder = reminder.copy(
            startDateTime = getUpdatedReminderStartDateTime(reminder)
        )

        viewModelScope.launch(ioDispatcher) {
            repository.updateReminder(updatedReminder)
        }
    }

    private fun completeRepeatReminderSeries(reminderState: ReminderState) {
        val reminder = reminderState.toReminder()

        removeReminderNotification(reminder)

        viewModelScope.launch(ioDispatcher) {
            repository.completeReminder(reminder.id)
        }
    }

    private fun deleteReminder(reminderState: ReminderState) {
        val reminder = reminderState.toReminder()

        removeReminderNotification(reminder)

        viewModelScope.launch(ioDispatcher) {
            repository.deleteReminder(reminder)
        }
    }

    private fun removeReminderNotification(reminder: Reminder) {
        notificationScheduler.removeDisplayingNotification(reminder.id.toInt())

        if (reminder.isNotificationSent) {
            notificationScheduler.cancelScheduledReminderNotification(reminder)
        }
    }

    private fun getUpdatedReminderStartDateTime(reminder: Reminder): ZonedDateTime {
        val repeatInterval = reminder.repeatInterval ?: return reminder.startDateTime

        val repeatDuration = when (repeatInterval.unit) {
            ChronoUnit.DAYS -> repeatInterval.amount.days
            else -> (repeatInterval.amount * DAYS_IN_WEEK).days
        }

        if (reminder.startDateTime.isAfter(ZonedDateTime.now())) {
            return reminder.startDateTime.plusSeconds(repeatDuration.inWholeSeconds)
        }

        val secondsToNewStartDateTime = ZonedDateTime.now()
            .toEpochSecond()
            .minus(reminder.startDateTime.toEpochSecond())
            .toDuration(DurationUnit.SECONDS)
            .div(repeatDuration)
            .floor()
            .inc()
            .times(repeatDuration)
            .inWholeSeconds

        return reminder.startDateTime.plusSeconds(secondsToNewStartDateTime)
    }
}
