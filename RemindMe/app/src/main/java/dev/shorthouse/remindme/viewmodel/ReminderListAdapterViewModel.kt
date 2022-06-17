package dev.shorthouse.remindme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.data.RepeatInterval
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.utilities.DATE_PATTERN
import dev.shorthouse.remindme.utilities.DAYS_IN_WEEK
import dev.shorthouse.remindme.utilities.ONE_INTERVAL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class ReminderListAdapterViewModel(
    val reminder: Reminder,
    private val repository: ReminderRepository,
) : ViewModel() {
    val reminderId get() = reminder.id

    val reminderStartDate = reminder.startDateTime
        .toLocalDate()
        .format(DateTimeFormatter.ofPattern(DATE_PATTERN))
        .toString()

    val reminderStartTime = reminder.startDateTime
        .toLocalTime()
        .toString()

    fun updateDoneReminder() {
        val updatedDoneReminder = when (reminder.repeatInterval) {
            null -> getCompletedSingleReminder(
                reminder.id,
                reminder.name,
                reminder.startDateTime,
                reminder.repeatInterval,
                reminder.notes,
                reminder.isNotificationSent
            )
            else -> getUpdatedRepeatReminder(
                reminder.id,
                reminder.name,
                reminder.startDateTime,
                reminder.repeatInterval,
                reminder.notes,
                reminder.isNotificationSent
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            repository.updateReminder(updatedDoneReminder)
        }
    }

    private fun getCompletedSingleReminder(
        id: Long,
        name: String,
        startDateTime: ZonedDateTime,
        repeatInterval: RepeatInterval?,
        notes: String?,
        isNotificationSent: Boolean,
    ): Reminder {
        return Reminder(
            id = id,
            name = name,
            startDateTime = startDateTime,
            repeatInterval = repeatInterval,
            notes = notes,
            isArchived = true,
            isNotificationSent = isNotificationSent,
        )
    }

    private fun getUpdatedRepeatReminder(
        id: Long,
        name: String,
        startDateTime: ZonedDateTime,
        repeatInterval: RepeatInterval,
        notes: String?,
        isNotificationSent: Boolean,
    ): Reminder {
        return Reminder(
            id = id,
            name = name,
            startDateTime = getUpdatedStartDateTime(startDateTime, repeatInterval),
            repeatInterval = repeatInterval,
            notes = notes,
            isArchived = false,
            isNotificationSent = isNotificationSent,
        )
    }

    private fun getUpdatedStartDateTime(
        startDateTime: ZonedDateTime,
        repeatInterval: RepeatInterval,
    ): ZonedDateTime {
        val period = Period.between(startDateTime.toLocalDate(), LocalDate.now())
        val timeValue = repeatInterval.timeValue

        return when (repeatInterval.timeUnit) {
            ChronoUnit.DAYS -> {
                val passedDays = period.days
                val passedIntervals = passedDays.div(timeValue)
                val nextInterval = passedIntervals.plus(ONE_INTERVAL)
                val daysUntilNextStart = timeValue * nextInterval
                startDateTime.plusDays(daysUntilNextStart)
            }
            else -> {
                val passedWeeks = period.days.div(DAYS_IN_WEEK)
                val passedIntervals = passedWeeks.div(timeValue)
                val nextInterval = passedIntervals.plus(ONE_INTERVAL)
                val weeksUntilNextStart = timeValue * nextInterval
                startDateTime.plusWeeks(weeksUntilNextStart)
            }
        }
    }
}
