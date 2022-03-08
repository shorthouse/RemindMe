package dev.shorthouse.habitbuilder.viewmodels

import android.util.Log
import androidx.lifecycle.*
import dev.shorthouse.habitbuilder.data.ReminderDao
import dev.shorthouse.habitbuilder.model.Reminder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

class AddReminderViewModel(private val reminderDao: ReminderDao
) : ViewModel() {

    companion object {
        private val dateFormatter = DateTimeFormatter.ofPattern("EEE dd MMM yyyy")
        private val dateTimeFormatter = DateTimeFormatter.ofPattern("EEE dd MMM yyyy HH:mm")

        private const val MAX_YEARS = 10
        private const val MAX_DAYS = 364
        private const val MAX_HOURS = 23
        private const val DAYS_IN_YEAR = 365
    }

    fun addReminder(
        name: String,
        startEpoch: Long,
        reminderInterval: Long?,
        notes: String,
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

    private fun convertStringToDate(reminderDateText: String): LocalDate {
        return LocalDate.parse(reminderDateText, dateFormatter)
    }

    private fun convertStringToDateTime(reminderDateText: String, reminderTimeText: String): LocalDateTime {
        val reminderDateTime = "$reminderDateText $reminderTimeText"
        return LocalDateTime.parse(reminderDateTime, dateTimeFormatter)
    }

    fun calculateReminderStartEpoch(reminderDateText: String, reminderTimeText: String): Long {
        val reminderDateTime = convertStringToDateTime(reminderDateText, reminderTimeText)
        return reminderDateTime.atZone(ZoneId.systemDefault()).toEpochSecond()
    }

    fun convertTimestampToDate(dateTimestamp: Long): String {
        return Instant.ofEpochMilli(dateTimestamp)
            .atZone(ZoneId.systemDefault())
            .format(dateFormatter)
    }

    fun convertReminderIntervalToSeconds(years: Long, days: Long, hours: Long): Long {
        return Duration.ofDays(years * DAYS_IN_YEAR).seconds +
                Duration.ofDays(days).seconds +
                Duration.ofHours(hours).seconds
    }

    fun isDetailValid(name: String, startDate: String, reminderTime: String, ): Boolean {
        return when {
            name.isBlank() -> false
            startDate.isBlank() -> false
            convertStringToDate(startDate).isBefore(LocalDate.now()) -> false
            reminderTime.isBlank() -> false
            convertStringToDateTime(startDate, reminderTime).isBefore(LocalDateTime.now()) -> false
            else -> true
        }
    }

    fun getDetailError(name: String, startDate: String, reminderTime: String): String {
        return when {
            name.isBlank() -> "The name cannot be empty."
            startDate.isBlank() -> "The start date cannot be empty."
            convertStringToDate(startDate).isBefore(LocalDate.now()) -> "The start date cannot be in the past."
            reminderTime.isBlank() -> "The start time cannot be empty."
            convertStringToDateTime(startDate, reminderTime).isBefore(LocalDateTime.now()) ->
                "The start time cannot be in the past."
            else -> ""
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

    fun getIntervalError(years: Long, days: Long, hours: Long): String {
        return when {
            years > MAX_YEARS -> "The maximum years interval is 10."
            days > MAX_DAYS -> "The maximum days interval is 364."
            hours > MAX_HOURS -> "The maximum hours interval is 23."
            years == 0L && days == 0L && hours == 0L -> "The interval must have a time period."
            else -> ""
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