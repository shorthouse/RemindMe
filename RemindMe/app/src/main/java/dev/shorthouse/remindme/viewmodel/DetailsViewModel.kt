package dev.shorthouse.remindme.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.data.RepeatInterval
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.utilities.DATE_INPUT_PATTERN
import dev.shorthouse.remindme.utilities.NotificationScheduler
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: ReminderRepository,
    private val notificationScheduler: NotificationScheduler,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    lateinit var reminder: Reminder

    fun getReminder(id: Long): LiveData<Reminder> {
        return repository.getReminder(id).asLiveData()
    }

    fun deleteReminder() {
        if (reminder.isNotificationSent) {
            cancelReminderNotification()
        }

        viewModelScope.launch(ioDispatcher) {
            repository.deleteReminder(reminder)
        }
    }

    fun completeReminder() {
        if (reminder.isNotificationSent) {
            cancelReminderNotification()
        }

        viewModelScope.launch(ioDispatcher) {
            repository.archiveReminder(reminder.id)
        }
    }

    fun getFormattedStartDate(reminder: Reminder): String {
        return reminder.startDateTime
            .toLocalDate()
            .format(DateTimeFormatter.ofPattern(DATE_INPUT_PATTERN))
            .toString()
    }

    fun getRepeatIntervalStringId(repeatInterval: RepeatInterval): Int {
        return when (repeatInterval.timeUnit) {
            ChronoUnit.DAYS -> R.plurals.interval_days
            else -> R.plurals.interval_weeks
        }
    }

    private fun cancelReminderNotification() {
        notificationScheduler.cancelExistingReminderNotification(reminder)
    }
}
