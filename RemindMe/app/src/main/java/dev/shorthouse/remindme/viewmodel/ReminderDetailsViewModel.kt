package dev.shorthouse.remindme.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.data.RepeatInterval
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.utilities.DATE_PATTERN
import dev.shorthouse.remindme.utilities.NotificationScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class ReminderDetailsViewModel @Inject constructor(
    private val repository: ReminderRepository,
    private val notificationScheduler: NotificationScheduler,
) : ViewModel() {

    lateinit var reminder: Reminder

    fun getReminder(id: Long): LiveData<Reminder> {
        return repository.getReminder(id).asLiveData()
    }

    fun deleteReminder() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteReminder(reminder)
        }
    }

    fun cancelReminderNotification() {
        notificationScheduler.cancelExistingReminderNotification(reminder)
    }

    fun getFormattedStartDate(reminder: Reminder?): String {
        if (reminder == null) return ""
        return reminder.startDateTime
            .toLocalDate()
            .format(DateTimeFormatter.ofPattern(DATE_PATTERN))
            .toString()
    }

    fun getFormattedStartTime(reminder: Reminder): String {
        return reminder.startDateTime.toLocalTime().toString()
    }

    fun getRepeatIntervalId(repeatInterval: RepeatInterval): Int {
        return when (repeatInterval.timeUnit) {
            ChronoUnit.DAYS -> R.plurals.interval_days
            else -> R.plurals.interval_weeks
        }
    }
}
