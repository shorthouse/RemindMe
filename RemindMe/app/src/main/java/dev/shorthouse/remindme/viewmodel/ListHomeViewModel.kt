package dev.shorthouse.remindme.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.utilities.DAYS_IN_WEEK
import dev.shorthouse.remindme.utilities.NotificationScheduler
import dev.shorthouse.remindme.utilities.floor
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
class ListHomeViewModel @Inject constructor(
    private val repository: ReminderRepository,
    private val notificationScheduler: NotificationScheduler,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    var selectedReminderState by mutableStateOf(ReminderState())

    fun completeOnetimeReminder(reminderState: ReminderState) {
        val reminder = reminderState.toReminder()

        removeReminderNotification(reminder)

        viewModelScope.launch(ioDispatcher) {
            repository.completeReminder(reminder.id)
        }
    }

    fun completeRepeatReminderOccurrence(reminderState: ReminderState) {
        val reminder = reminderState.toReminder()

        val updatedReminder = reminder.copy(
            startDateTime = getUpdatedReminderStartDateTime(reminder)
        )

        viewModelScope.launch(ioDispatcher) {
            repository.updateReminder(updatedReminder)
        }
    }

    fun completeRepeatReminderSeries(reminderState: ReminderState) {
        val reminder = reminderState.toReminder()

        removeReminderNotification(reminder)

        viewModelScope.launch(ioDispatcher) {
            repository.completeReminder(reminder.id)
        }
    }

    fun deleteReminder(reminderState: ReminderState) {
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
