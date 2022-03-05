package dev.shorthouse.habitbuilder.viewmodels

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

    fun addReminder(
        name: String,
        startEpoch: Long,
        reminderInterval: Long,
        notes: String,
    ) {
        val reminder = Reminder(
            name = name,
            startEpoch = startEpoch,
            repeatInterval = reminderInterval,
            notes = notes,
        )

        viewModelScope.launch(Dispatchers.IO) {
            reminderDao.insert(reminder)
        }
    }

    fun getFormattedDate(dateTimestamp: Long): String {
        val dateFormatter = SimpleDateFormat("EEE dd MMM yyyy", Locale.getDefault())
        return dateFormatter.format(dateTimestamp)
    }

    fun getReminderDate(reminderDateText: String): LocalDate {
        val dateFormatter = DateTimeFormatter.ofPattern("EEE dd MMM yyyy")
        return LocalDate.parse(reminderDateText, dateFormatter)
    }

    fun getReminderDateTime(reminderDateText: String, reminderTimeText: String): LocalDateTime {
        val reminderDateTime = "$reminderDateText $reminderTimeText"
        val formatter = DateTimeFormatter.ofPattern("EEE dd MMM yyyy HH:mm")
        return LocalDateTime.parse(reminderDateTime, formatter)
    }

    fun getReminderStartEpoch(reminderDateTime: LocalDateTime): Long {
        return reminderDateTime.atZone(ZoneId.systemDefault()).toEpochSecond()
    }

    fun getSecondsUntilReminder(startEpoch: Long): Long {
        val nowEpoch = Instant.now().epochSecond
        return startEpoch - nowEpoch
    }

    fun getReminderInterval(years: Long, days: Long, hours: Long): Long {
        val daysInYear = 365
        return Duration.ofDays(years * daysInYear).seconds +
                Duration.ofDays(days).seconds +
                Duration.ofHours(hours).seconds
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