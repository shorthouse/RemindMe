package dev.shorthouse.remindme.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.data.ReminderDao
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.utilities.DAYS_IN_YEAR
import dev.shorthouse.remindme.utilities.MAX_DAYS
import dev.shorthouse.remindme.utilities.MAX_HOURS
import dev.shorthouse.remindme.utilities.MAX_YEARS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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

    private fun convertStringToDateTime(dateTimeText: String): LocalDateTime {
        return LocalDateTime.parse(dateTimeText, dateTimeFormatter)
    }

    fun calculateReminderStartEpoch(dateTimeText: String): Long {
        val reminderDateTime = convertStringToDateTime(dateTimeText)
        return reminderDateTime.atZone(ZoneId.systemDefault()).toEpochSecond()
    }

    fun convertInstantToDateString(instant: Instant): String {
        return instant
            .atZone(ZoneId.systemDefault())
            .format(dateFormatter)
    }

    fun convertReminderIntervalToSeconds(years: Long, days: Long, hours: Long): Long {
        return Duration.ofDays(years * DAYS_IN_YEAR).seconds +
                Duration.ofDays(days).seconds +
                Duration.ofHours(hours).seconds
    }

    fun getCurrentTimeNextHour(): Int {
        return LocalDateTime
            .ofInstant(
                Instant.now(),
                ZoneId.systemDefault()
            )
            .toLocalTime().hour.plus(1)
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