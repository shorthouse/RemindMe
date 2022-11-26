package dev.shorthouse.remindme.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.model.RepeatInterval
import dev.shorthouse.remindme.utilities.DATE_INPUT_PATTERN
import dev.shorthouse.remindme.utilities.DATE_TIME_INPUT_PATTERN
import dev.shorthouse.remindme.utilities.NotificationScheduler
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val repository: ReminderRepository,
    private val notificationScheduler: NotificationScheduler,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    fun getReminder(id: Long): LiveData<Reminder> {
        return repository.getReminder(id).asLiveData()
    }

    fun addReminder(reminder: Reminder) {
        viewModelScope.launch(ioDispatcher) {
            val reminderId = repository.insertReminder(reminder)

            if (reminder.isNotificationSent) {
                reminder.id = reminderId
                scheduleReminderNotification(reminder)
            }
        }
    }

    fun editReminder(reminder: Reminder) {
        viewModelScope.launch(ioDispatcher) {
            repository.updateReminder(reminder)
            cancelExistingReminderNotification(reminder)

            if (reminder.isNotificationSent) {
                scheduleReminderNotification(reminder)
            }
        }
    }

    private fun scheduleReminderNotification(reminder: Reminder) {
        notificationScheduler.scheduleReminderNotification(reminder)
    }

    private fun cancelExistingReminderNotification(reminder: Reminder) {
        notificationScheduler.cancelExistingReminderNotification(reminder)
    }

    fun getFormattedDate(zonedDateTime: ZonedDateTime): String {
        return zonedDateTime
            .toLocalDate()
            .format(DateTimeFormatter.ofPattern(DATE_INPUT_PATTERN))
            .toString()
    }

    fun getFormattedTime(zonedDateTime: ZonedDateTime): String {
        return zonedDateTime
            .toLocalTime()
            .toString()
    }

    fun getFormattedTimeNextHour(zonedDateTime: ZonedDateTime): String {
        return zonedDateTime
            .truncatedTo(ChronoUnit.HOURS)
            .plusHours(1)
            .toLocalTime()
            .toString()
    }

    fun getReminderNotes(reminder: Reminder): String {
        return reminder.notes.orEmpty()
    }

    fun getRepeatValue(repeatInterval: RepeatInterval): String {
        return repeatInterval.timeValue.toString()
    }

    fun convertDateTimeStringToDateTime(dateText: String, timeText: String): ZonedDateTime {
        return LocalDateTime.parse("$dateText $timeText", DateTimeFormatter.ofPattern(DATE_TIME_INPUT_PATTERN))
            .atZone(ZoneId.systemDefault())
    }

    fun convertEpochMilliToDate(dateTimestamp: Long): String {
        return Instant.ofEpochMilli(dateTimestamp)
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern(DATE_INPUT_PATTERN))
    }

    fun formatTimePickerTime(hour: Int, minute: Int): String {
        return LocalTime.of(hour, minute).toString()
    }

    fun getRepeatUnitFromRadioId(repeatUnitCheckedRadioId: Int): ChronoUnit {
        return when (repeatUnitCheckedRadioId) {
            R.id.repeat_unit_days -> ChronoUnit.DAYS
            else -> ChronoUnit.WEEKS
        }
    }

    fun getRadioIdFromRepeatUnit(repeatInterval: RepeatInterval): Int {
        return when (repeatInterval.timeUnit) {
            ChronoUnit.DAYS -> R.id.repeat_unit_days
            else -> R.id.repeat_unit_weeks
        }
    }

    fun getRepeatInterval(isRepeatReminder: Boolean, timeValue: Long, timeUnit: ChronoUnit): RepeatInterval? {
        if (!isRepeatReminder) return null

        return RepeatInterval(timeValue, timeUnit)
    }

    fun getReminderName(name: String): String {
        return name.trim()
    }

    fun getReminderNotes(notes: String): String? {
        return notes.trim().ifBlank { null }
    }

    fun isNameValid(name: String): Boolean {
        return name.isNotBlank()
    }

    fun isRepeatIntervalValid(repeatIntervalValue: Long): Boolean {
        return repeatIntervalValue > 0L
    }

    fun isStartTimeValid(startDate: String, startTime: String): Boolean {
        return convertDateTimeStringToDateTime(startDate, startTime).isAfter(ZonedDateTime.now())
    }

    fun getRepeatValueText(repeatValueText: String): Int {
        return repeatValueText.toIntOrNull() ?: 0
    }
}
