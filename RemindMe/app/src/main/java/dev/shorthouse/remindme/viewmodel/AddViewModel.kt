package dev.shorthouse.remindme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.model.InputReminder
import dev.shorthouse.remindme.model.InputRepeatInterval
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
class AddViewModel @Inject constructor(
    private val repository: ReminderRepository,
    private val notificationScheduler: NotificationScheduler,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    val addReminderInitialValues = InputReminder(
        name = "",
        startDate = getFormattedDateToday(),
        startTime = getFormattedTimeNextHour(),
        isNotificationSent = false,
        repeatInterval = InputRepeatInterval(
            R.plurals.interval_days,
            1
        ),
        notes = null,
        isComplete = false
    )

    fun addReminder(reminder: Reminder) {
        viewModelScope.launch(ioDispatcher) {
            val reminderId = repository.insertReminder(reminder)

            if (reminder.isNotificationSent) {
                reminder.id = reminderId
                notificationScheduler.scheduleReminderNotification(reminder)
            }
        }
    }

    private fun getFormattedDateToday(): String {
        return ZonedDateTime.now()
            .toLocalDate()
            .format(DateTimeFormatter.ofPattern(DATE_INPUT_PATTERN))
            .toString()
    }

    private fun getFormattedTimeNextHour(): String {
        return ZonedDateTime.now()
            .truncatedTo(ChronoUnit.HOURS)
            .plusHours(1)
            .toLocalTime()
            .toString()
    }
}
