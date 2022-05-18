package dev.shorthouse.remindme.viewmodel

import androidx.lifecycle.*
import dev.shorthouse.remindme.data.ReminderDao
import dev.shorthouse.remindme.data.RepeatInterval
import dev.shorthouse.remindme.model.Reminder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class AddReminderViewModel(
    private val reminderDao: ReminderDao
) : ViewModel() {

    private val dateFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy")
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm")
    val newReminder = MutableLiveData<Reminder?>()

    fun getReminder(id: Long): LiveData<Reminder> {
        return reminderDao.getReminder(id).asLiveData()
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
            reminder.id = reminderDao.insert(reminder)
            newReminder.postValue(reminder)
        }
    }

    fun updateReminder(
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
            reminderDao.update(reminder)
            newReminder.postValue(reminder)
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
            .truncatedTo(ChronoUnit.HOURS)
            .plusHours(1)
            .toLocalTime()
            .toString()
    }

    fun getIsRepeatChecked(reminder: Reminder?): Boolean {
        return if (reminder == null) false else reminder.repeatInterval != null
    }

    fun isNameValid(name: String): Boolean {
        return name.isNotBlank()
    }

    fun isStartTimeValid(startDate: String, startTime: String): Boolean {
        val startDateTime = convertDateTimeStringToDateTime(startDate, startTime)
        return startDateTime.isAfter(ZonedDateTime.now())
    }

    fun isRepeatIntervalValid(repeatIntervalValue: Long): Boolean {
        return repeatIntervalValue > 0
    }

    fun clearLiveData() {
        newReminder.postValue(null)
    }

    fun getRepeatIntervalValue(reminder: Reminder?): Long {
        return if (reminder?.repeatInterval == null) 1L else reminder.repeatInterval.timeValue
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

    fun getReminderNotes(notes: String): String? = notes.ifBlank { null }
}

class AddEditReminderViewModelFactory(
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