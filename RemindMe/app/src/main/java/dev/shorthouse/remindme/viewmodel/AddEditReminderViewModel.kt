package dev.shorthouse.remindme.viewmodel

import androidx.lifecycle.*
import dev.shorthouse.remindme.BaseApplication
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.data.ReminderDao
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.utilities.DAYS_IN_WEEK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class AddReminderViewModel(
    application: BaseApplication,
    private val reminderDao: ReminderDao
) : ViewModel() {

    private val dateFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy")
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm")

    fun getReminder(id: Long): LiveData<Reminder> {
        return reminderDao.getReminder(id).asLiveData()
    }

    fun addReminder(
        name: String,
        startDateTime: ZonedDateTime,
        repeatInterval: Pair<Int, ChronoUnit>?,
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

        viewModelScope.launch(Dispatchers.IO) { reminderDao.insert(reminder) }
    }

    fun updateReminder(
        id: Long,
        name: String,
        startDateTime: ZonedDateTime,
        repeatInterval: Pair<Int, ChronoUnit>?,
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

        viewModelScope.launch(Dispatchers.IO) { reminderDao.update(reminder) }
    }

    fun getRepeatIntervalMillis(repeatInterval: Pair<Int, ChronoUnit>): Long {
        val intervalTimeValue = repeatInterval.first
        val intervalTimeUnit = repeatInterval.second

        return when (intervalTimeUnit) {
            ChronoUnit.DAYS -> Duration.ofDays(intervalTimeValue.toLong()).toMillis()
            else -> Duration.ofDays(intervalTimeValue.toLong() * DAYS_IN_WEEK).toMillis()
        }
    }

    fun convertDateTimeStringToDateTime(dateText: String, timeText: String): ZonedDateTime {
        return LocalDateTime.parse(
            "$dateText $timeText",
            dateTimeFormatter
        )
            .atZone(ZoneId.systemDefault())
    }

    fun getStartDate(reminder: Reminder?): String {
        return when (reminder) {
            null -> ZonedDateTime.now().toLocalDate().format(dateFormatter).toString()
            else -> reminder.startDateTime.toLocalDate().format(dateFormatter).toString()
        }
    }

    fun convertTimestampToDateString(dateTimestamp: Long): String {
        return Instant.ofEpochMilli(dateTimestamp)
            .atZone(ZoneId.systemDefault())
            .format(dateFormatter)
    }

    fun getStartTime(reminder: Reminder?): String {
        return when (reminder) {
            null -> getCurrentTimeNextHour()
            else -> reminder.startDateTime.toLocalTime().toString()
        }
    }

    private fun getCurrentTimeNextHour(): String {
        return ZonedDateTime.now()
            .truncatedTo(ChronoUnit.HOURS).plusHours(1)
            .toLocalTime().toString()
    }

    fun getIsRepeatChecked(reminder: Reminder?): Boolean {
        return if (reminder == null) false else reminder.repeatInterval != null
    }

    fun isDetailValid(name: String, startDateTime: ZonedDateTime): Boolean {
        return when {
            name.isBlank() -> false
            startDateTime.isBefore(ZonedDateTime.now()) -> false
            else -> true
        }
    }

    fun getDetailError(name: String): Int {
        return when {
            name.isBlank() -> R.string.error_name_empty
            else -> R.string.error_time_past
        }
    }

    fun getReminderStartDateTimeMillis(reminderStartDateTime: ZonedDateTime): Long {
        return reminderStartDateTime.toInstant().toEpochMilli()
    }
}

class AddEditReminderViewModelFactory(
    private val application: BaseApplication,
    private val reminderDao: ReminderDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddReminderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddReminderViewModel(application, reminderDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}