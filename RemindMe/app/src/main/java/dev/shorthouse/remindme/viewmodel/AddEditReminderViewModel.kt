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
import java.time.*
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

    val defaultRepeatValue = "1"
    val defaultRepeatUnit = ChronoUnit.DAYS

    val repeatPeriodChronoUnitMap = mapOf(
        "day" to ChronoUnit.DAYS,
        "days" to ChronoUnit.DAYS,
        "week" to ChronoUnit.WEEKS,
        "weeks" to ChronoUnit.WEEKS,
    )

    fun getReminder(id: Long): LiveData<Reminder> {
        return repository.getReminder(id).asLiveData()
    }

    fun addReminder(
        name: String,
        startDateTime: ZonedDateTime,
        repeatInterval: RepeatInterval?,
        notes: String?,
        isArchived: Boolean,
        isNotificationSent: Boolean
    ) {
        val reminder = Reminder(
            name = name,
            startDateTime = startDateTime,
            repeatInterval = repeatInterval,
            notes = notes,
            isArchived = isArchived,
            isNotificationSent = isNotificationSent
        )

        viewModelScope.launch(Dispatchers.IO) {
            val reminderId = repository.insertReminder(reminder)

            if (reminder.isNotificationSent) {
                reminder.id = reminderId
                scheduleReminderNotification(reminder)
            }
        }
    }

    fun editReminder(
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
        return reminder.notes ?: ""
    }

    fun getRepeatValue(reminder: Reminder): String {
        return if (reminder.repeatInterval == null) "1" else reminder.repeatInterval.timeValue.toString()
    }

    fun getRepeatUnit(reminder: Reminder): ChronoUnit {
        return if (reminder.repeatInterval == null) ChronoUnit.DAYS else reminder.repeatInterval.timeUnit
    }

    fun getIsNotificationSent(reminder: Reminder?): Boolean {
        return reminder?.isNotificationSent ?: false
    }

    fun convertDateTimeStringToDateTime(dateText: String, timeText: String): ZonedDateTime {
        return LocalDateTime.parse(
            "$dateText $timeText",
            DateTimeFormatter.ofPattern(DATE_TIME_INPUT_PATTERN)
        )
            .atZone(ZoneId.systemDefault())
    }

    fun formatDatePickerDate(dateTimestamp: Long): String {
        return Instant.ofEpochMilli(dateTimestamp)
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern(DATE_INPUT_PATTERN))
    }

    fun formatTimePickerTime(hour: Int, minute: Int): String {
        return LocalTime.of(hour, minute).toString()
    }

    fun getRepeatInterval(timeValue: Long, repeatUnitString: String): RepeatInterval? {
        val repeatUnit = repeatPeriodChronoUnitMap[repeatUnitString]
        return if (repeatUnit == null) null else RepeatInterval(timeValue, repeatUnit)
    }

    fun getReminderNotes(notes: String): String? {
        return notes.ifBlank { null }
    }

    fun isNameValid(name: String): Boolean {
        return name.isNotBlank()
    }

    fun isRepeatIntervalValid(repeatIntervalValue: Long): Boolean {
        return repeatIntervalValue > 0
    }

    fun isStartTimeValid(startDate: String, startTime: String): Boolean {
        return convertDateTimeStringToDateTime(startDate, startTime).isAfter(ZonedDateTime.now())
    }
}
