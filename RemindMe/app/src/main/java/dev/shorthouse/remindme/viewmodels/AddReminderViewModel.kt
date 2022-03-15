package dev.shorthouse.remindme.viewmodels

import androidx.lifecycle.*
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.data.ReminderDao
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.utilities.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class AddReminderViewModel(
    private val reminderDao: ReminderDao
) : ViewModel() {

    companion object {
        private val dateFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy")
        private val dateTimeFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm")
    }

    fun addReminder(
        name: String,
        startEpoch: Long,
        reminderInterval: Long?,
        notes: String?,
        isArchived: Boolean,
    ) {
        val reminder = Reminder(
            name = name,
            startEpoch = startEpoch,
            repeatInterval = reminderInterval,
            notes = notes,
            isArchived = isArchived
        )

        viewModelScope.launch(Dispatchers.IO) {
            reminderDao.insert(reminder)
        }
    }

    fun updateReminder(
        id: Long,
        name: String,
        startEpoch: Long,
        reminderInterval: Long?,
        notes: String?,
        isArchived: Boolean
    ) {
        val reminder = Reminder(
            id = id,
            name = name,
            startEpoch = startEpoch,
            repeatInterval = reminderInterval,
            notes = notes,
            isArchived = isArchived
        )

        viewModelScope.launch(Dispatchers.IO) {
            reminderDao.update(reminder)
        }
    }

    fun getReminder(id: Long): LiveData<Reminder> {
        return reminderDao.getReminder(id).asLiveData()
    }

    private fun convertStringToDateTime(dateTimeText: String): LocalDateTime {
        return LocalDateTime.parse(dateTimeText, dateTimeFormatter)
    }

    fun calculateReminderStartEpoch(dateTimeText: String): Long {
        val reminderDateTime = convertStringToDateTime(dateTimeText)
        return reminderDateTime.atZone(ZoneId.systemDefault()).toEpochSecond()
    }

    fun getStartDate(reminder: Reminder?): String {
        return when (reminder) {
            null -> convertInstantToDateString(Instant.now())
            else -> convertInstantToDateString(Instant.ofEpochSecond(reminder.startEpoch))
        }
    }

    fun convertInstantToDateString(instant: Instant): String {
        return instant
            .atZone(ZoneId.systemDefault())
            .format(dateFormatter)
    }

    fun getStartTime(reminder: Reminder?): String {
        return when (reminder) {
            null -> getCurrentTimeNextHour()
            else -> convertEpochToTime(reminder.startEpoch)
        }
    }

    fun getIsRepeatChecked(reminder: Reminder?): Boolean {
        return if (reminder == null) false else reminder.repeatInterval != null
    }

    fun convertReminderIntervalToSeconds(years: Long, days: Long, hours: Long): Long {
        return Duration.ofDays(years * DAYS_IN_YEAR).seconds +
                Duration.ofDays(days).seconds +
                Duration.ofHours(hours).seconds
    }

    private fun convertEpochToTime(epoch: Long): String {
        return LocalDateTime.ofInstant(
            Instant.ofEpochSecond(epoch),
            ZoneId.systemDefault()
        )
            .toLocalTime().toString()
    }

    private fun getCurrentTimeNextHour(): String {
        return LocalDateTime.ofInstant(
            Instant.now(),
            ZoneId.systemDefault()
        )
            .truncatedTo(ChronoUnit.HOURS).plusHours(1)
            .toLocalTime().toString()
    }

    fun getRepeatIntervalYears(repeatInterval: Long?): String {
        if (repeatInterval == null) return ""
        return Duration.ofSeconds(repeatInterval).toYearPart().toString()
    }

    fun getRepeatIntervalDays(repeatInterval: Long?): String {
        if (repeatInterval == null) return ""
        return Duration.ofSeconds(repeatInterval).toDayPart().toString()
    }

    fun getRepeatIntervalHours(repeatInterval: Long?): String {
        if (repeatInterval == null) return ""
        return Duration.ofSeconds(repeatInterval).toHourPart().toString()
    }

    fun isDetailValid(name: String, startDate: String, reminderTime: String): Boolean {
        return when {
            name.isBlank() -> false
            convertStringToDateTime("$startDate $reminderTime").isBefore(LocalDateTime.now()) -> false
            else -> true
        }
    }

    fun getDetailError(name: String): Int {
        return when {
            name.isBlank() -> R.string.error_name_empty
            else -> R.string.error_time_past
        }
    }

    fun isIntervalValid(isRepeatReminder: Boolean, years: Long, days: Long, hours: Long): Boolean {
        return when {
            !isRepeatReminder -> true
            years > MAX_YEARS -> false
            days > MAX_DAYS -> false
            hours > MAX_HOURS -> false
            years == 0L && days == 0L && hours == 0L -> false
            else -> true
        }
    }

    fun getIntervalError(years: Long, days: Long, hours: Long): Int {
        return when {
            years > MAX_YEARS -> R.string.error_years_max
            days > MAX_DAYS -> R.string.error_days_max
            hours > MAX_HOURS -> R.string.error_hours_max
            else -> R.string.error_interval_zero
        }
    }

}

class AddReminderViewModelFactory(
    private val reminderDao: ReminderDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddReminderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddReminderViewModel(reminderDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}