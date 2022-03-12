package dev.shorthouse.habitbuilder.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.shorthouse.habitbuilder.R
import dev.shorthouse.habitbuilder.data.ReminderDao
import dev.shorthouse.habitbuilder.model.Reminder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.*
import java.time.format.DateTimeFormatter

class AddReminderViewModel(
    private val reminderDao: ReminderDao
) : ViewModel() {

    companion object {
        private val dateFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy")
        private val dateTimeFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm")

        private const val MAX_YEARS = 10
        private const val MAX_DAYS = 364
        private const val MAX_HOURS = 23
        private const val DAYS_IN_YEAR = 365
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

    private fun convertStringToDate(reminderDateText: String): LocalDate {
        return LocalDate.parse(reminderDateText, dateFormatter)
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

    fun isDetailValid(name: String, startDate: String, reminderTime: String): Boolean {
        return when {
            name.isBlank() -> false
            convertStringToDate(startDate).isBefore(LocalDate.now()) -> false
            convertStringToDateTime("$startDate $reminderTime").isBefore(LocalDateTime.now()) -> false
            else -> true
        }
    }

    fun getDetailError(name: String, startDate: String): Int {
        return when {
            name.isBlank() -> R.string.error_name_empty
            convertStringToDate(startDate).isBefore(LocalDate.now()) -> R.string.error_date_past
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

    fun getFormattedTimeNextHour(): String {
        val now = Instant.now()
        val zoneId = ZoneId.systemDefault()
        val localTime = LocalDateTime.ofInstant(now, zoneId).toLocalTime()
        val nextHour = localTime.hour.plus(1)
        return "${nextHour}:00"
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