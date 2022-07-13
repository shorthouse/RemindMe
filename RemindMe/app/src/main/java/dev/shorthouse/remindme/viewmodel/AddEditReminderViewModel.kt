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

    var isEditReminder = false
    var isAddReminder = isEditReminder.not()

    val repeatPeriodChronoUnitMap = mapOf(
        "day" to ChronoUnit.DAYS,
        "days" to ChronoUnit.DAYS,
        "week" to ChronoUnit.WEEKS,
        "weeks" to ChronoUnit.WEEKS,
    )

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
            if (isEditReminder) {
                repository.updateReminder(reminder)
                cancelExistingReminderNotification(reminder)
            } else {
                repository.insertReminder(reminder)
            }
            scheduleReminderNotification(reminder)
        }
    }


    private fun scheduleReminderNotification(reminder: Reminder) {
        if (reminder.isNotificationSent) {
            notificationScheduler.scheduleReminderNotification(reminder)
        }
    }

    private fun cancelExistingReminderNotification(reminder: Reminder) {
        notificationScheduler.cancelExistingReminderNotification(reminder)
    }


    fun formatReminderStartDate(reminder: Reminder): String {
        return reminder.startDateTime
            .toLocalDate()
            .format(DateTimeFormatter.ofPattern(DATE_INPUT_PATTERN))
            .toString()
    }

    fun formatReminderStartTime(reminder: Reminder): String {
        return reminder.startDateTime
            .toLocalTime()
            .toString()
    }

    fun getReminderNotes(reminder: Reminder): String {
        return reminder.notes ?: ""
    }

    fun getStartDateNow(): String {
        return ZonedDateTime.now()
            .toLocalDate()
            .format(DateTimeFormatter.ofPattern(DATE_INPUT_PATTERN))
            .toString()
    }

    fun getStartTimeNextHour(): String {
        return ZonedDateTime.now()
            .truncatedTo(ChronoUnit.HOURS)
            .plusHours(1)
            .toLocalTime()
            .toString()
    }

    fun getIsNotificationSent(reminder: Reminder?): Boolean {
        return reminder?.isNotificationSent ?: false
    }

    fun getRepeatValue(reminder: Reminder): String {
        return if (reminder.repeatInterval == null) "1" else reminder.repeatInterval.timeValue.toString()
    }

    fun getRepeatUnit(reminder: Reminder): ChronoUnit {
        return if (reminder.repeatInterval == null) ChronoUnit.DAYS else reminder.repeatInterval.timeUnit
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

    fun getRepeatInterval(
        timeValue: Long,
        repeatUnitString: String
    ): RepeatInterval? {
        val repeatUnit = repeatPeriodChronoUnitMap[repeatUnitString]
        return if (repeatUnit == null) {
            null
        } else {
            RepeatInterval(timeValue, repeatUnit)
        }
    }

    fun getReminderNotes(notes: String): String? = notes.ifBlank { null }
    fun isNameValid(name: String) = name.isNotBlank()
    fun isRepeatIntervalValid(repeatIntervalValue: Long) = repeatIntervalValue > 0
    fun isStartTimeValid(startDate: String, startTime: String) =
        convertDateTimeStringToDateTime(startDate, startTime).isAfter(ZonedDateTime.now())

}
