package dev.shorthouse.remindme.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.model.RepeatInterval
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.utilities.DATE_INPUT_PATTERN
import dev.shorthouse.remindme.utilities.NotificationScheduler
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: ReminderRepository,
    private val notificationScheduler: NotificationScheduler,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    state: SavedStateHandle
) : ViewModel() {
    private val reminderId = state.get<Long>("id") ?: 1L

    val reminder = repository.getReminder(reminderId).asLiveData()

    fun getReminder(id: Long): LiveData<Reminder> {
        return repository.getReminder(id).asLiveData()
    }

    fun deleteReminder() {
        reminder.value?.let {
            if (it.isNotificationSent) {
                cancelReminderNotification()
            }

            viewModelScope.launch(ioDispatcher) {
                repository.deleteReminder(it)
            }
        }
    }

    fun completeReminder() {
        reminder.value?.let {
            if (it.isNotificationSent) {
                cancelReminderNotification()
            }

            viewModelScope.launch(ioDispatcher) {
                repository.completeReminder(it.id)
            }
        }
    }

    fun getRepeatIntervalStringId(repeatInterval: RepeatInterval): Int {
        return when (repeatInterval.timeUnit) {
            ChronoUnit.DAYS -> R.plurals.interval_days
            else -> R.plurals.interval_weeks
        }
    }

    fun getFormattedDate(zonedDateTime: ZonedDateTime): String {
        return zonedDateTime
            .toLocalDate()
            .format(DateTimeFormatter.ofPattern(DATE_INPUT_PATTERN))
            .toString()
    }

    private fun cancelReminderNotification() {
        reminder.value?.let {
            notificationScheduler.cancelExistingReminderNotification(it)
        }
    }
}
