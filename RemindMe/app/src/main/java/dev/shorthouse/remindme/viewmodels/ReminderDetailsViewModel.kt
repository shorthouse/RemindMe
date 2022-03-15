package dev.shorthouse.remindme.viewmodels

import androidx.lifecycle.*
import dev.shorthouse.remindme.data.ReminderDao
import dev.shorthouse.remindme.model.Reminder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ReminderDetailsViewModel(
    private val reminderDao: ReminderDao
) : ViewModel() {

    companion object {
        private val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
    }

    fun getReminder(id: Long): LiveData<Reminder> {
        return reminderDao.getReminder(id).asLiveData()
    }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch(Dispatchers.IO) {
            reminderDao.delete(reminder)
        }
    }

    fun convertEpochToDate(epoch: Long): String {
        return Instant.ofEpochSecond(epoch)
            .atZone(ZoneId.systemDefault())
            .format(dateFormatter)
    }

    fun convertEpochToTime(epoch: Long): String {
        return getLocalDateTime(epoch).toLocalTime().toString()
    }

    private fun getLocalDateTime(epoch: Long): LocalDateTime {
        return LocalDateTime.ofInstant(
            Instant.ofEpochSecond(epoch),
            ZoneId.systemDefault()
        )
    }

}

class ReminderDetailsViewModelFactory(
    private val reminderDao: ReminderDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReminderDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReminderDetailsViewModel(reminderDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}