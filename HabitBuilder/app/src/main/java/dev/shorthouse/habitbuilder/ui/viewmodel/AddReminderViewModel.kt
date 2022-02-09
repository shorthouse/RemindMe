package dev.shorthouse.habitbuilder.ui.viewmodel

import androidx.lifecycle.*
import dev.shorthouse.habitbuilder.data.ReminderDao
import dev.shorthouse.habitbuilder.model.Reminder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class AddReminderViewModel(private val reminderDao: ReminderDao
) : ViewModel() {

    fun addReminder(
        name: String,
        reminderEpoch: Long,
        notes: String,
    ) {
        val reminder = Reminder(
            name = name,
            reminderEpoch = reminderEpoch,
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
        return  LocalDateTime.parse(reminderDateTime, formatter)
    }

    fun getReminderEpoch(reminderDateTime: LocalDateTime): Long {
        return reminderDateTime.atZone(ZoneId.systemDefault()).toEpochSecond()
    }

    fun getSecondsUntilReminder(reminderEpoch: Long): Long {
        val nowEpoch = Instant.now().epochSecond
        return reminderEpoch - nowEpoch
    }

}

class ReminderViewModelFactory(
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