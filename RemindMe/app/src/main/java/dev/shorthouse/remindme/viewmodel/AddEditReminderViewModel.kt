package dev.shorthouse.remindme.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.data.RepeatInterval
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.utilities.DATE_INPUT_PATTERN
import dev.shorthouse.remindme.utilities.DATE_TIME_INPUT_PATTERN
import dev.shorthouse.remindme.utilities.NotificationScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class AddEditReminderViewModel @Inject constructor(
    private val repository: ReminderRepository,
    private val notificationScheduler: NotificationScheduler,
) : ViewModel() {

    fun getReminder(id: Long): LiveData<Reminder> {
        return repository.getReminder(id).asLiveData()
    }

    fun saveReminder(
        id: Long,
        name: String,
        startDateTime: ZonedDateTime,
        repeatInterval: RepeatInterval?,
        notes: String?,
        isArchived: Boolean,
        isNotificationSent: Boolean
    ) {
        val reminder = Reminder(
            id = id,
            name = name,
            startDateTime = startDateTime,
            repeatInterval = repeatInterval,
            notes = notes,
            isArchived = isArchived,
            isNotificationSent = isNotificationSent
        )

        viewModelScope.launch(Dispatchers.IO) {
            if (isUpdatedReminder(reminder)) {
                repository.updateReminder(reminder)
                cancelExistingReminderNotification(reminder)
            } else {
                repository.insertReminder(reminder)
            }
            scheduleReminderNotification(reminder)
        }
    }

    private fun scheduleReminderNotification(reminder: Reminder) {
        if (!reminder.isNotificationSent) return
        notificationScheduler.scheduleReminderNotification(reminder)
    }

    private fun cancelExistingReminderNotification(reminder: Reminder) {
        if (isUpdatedReminder(reminder)) return
        notificationScheduler.cancelExistingReminderNotification(reminder)
    }

    fun getStartDate(reminder: Reminder?): String {
        val startDateTime = when (reminder) {
            null -> ZonedDateTime.now()
            else -> reminder.startDateTime
        }

        return startDateTime
            .toLocalDate()
            .format(DateTimeFormatter.ofPattern(DATE_INPUT_PATTERN))
            .toString()
    }

    fun getStartTime(reminder: Reminder?): String {
        val startTime = when (reminder) {
            null -> ZonedDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(1)
            else -> reminder.startDateTime
        }

        return startTime.toLocalTime().toString()
    }

    fun getRepeatIntervalValue(reminder: Reminder?): Long {
        return if (reminder?.repeatInterval == null) 1L else reminder.repeatInterval.timeValue
    }

    fun convertDateTimeStringToDateTime(dateText: String, timeText: String): ZonedDateTime {
        return LocalDateTime.parse(
            "$dateText $timeText",
            DateTimeFormatter.ofPattern(DATE_TIME_INPUT_PATTERN)
        )
            .atZone(ZoneId.systemDefault())
    }

    fun convertTimestampToDateString(dateTimestamp: Long): String {
        return Instant.ofEpochMilli(dateTimestamp)
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern(DATE_INPUT_PATTERN))
    }

    fun getIsRepeatChecked(reminder: Reminder?): Boolean {
        return if (reminder == null) false else reminder.repeatInterval != null
    }

    fun getRepeatInterval(
        isRepeatReminder: Boolean,
        timeValue: Long,
        timeUnitString: String
    ): RepeatInterval? {
        if (!isRepeatReminder) return null

        val timeUnit = when (timeUnitString) {
            in "days" -> ChronoUnit.DAYS
            else -> ChronoUnit.WEEKS
        }

        return RepeatInterval(timeValue, timeUnit)
    }

    private fun isUpdatedReminder(reminder: Reminder) = reminder.id != 0L
    fun getReminderNotes(notes: String): String? = notes.ifBlank { null }
    fun isNameValid(name: String) = name.isNotBlank()
    fun isRepeatIntervalValid(repeatIntervalValue: Long) = repeatIntervalValue > 0
    fun isStartTimeValid(startDate: String, startTime: String) =
        convertDateTimeStringToDateTime(startDate, startTime).isAfter(ZonedDateTime.now())
}
