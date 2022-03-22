package dev.shorthouse.remindme.viewmodel

import androidx.lifecycle.*
import androidx.work.WorkManager
import dev.shorthouse.remindme.BaseApplication
import dev.shorthouse.remindme.data.ReminderDao
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.utilities.NOTIFICATION_UNIQUE_WORK_NAME_PREFIX
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderDetailsViewModel(
    application: BaseApplication,
    private val reminderDao: ReminderDao
) : ViewModel() {

    private val workManager = WorkManager.getInstance(application)

    fun getReminder(id: Long): LiveData<Reminder> {
        return reminderDao.getReminder(id).asLiveData()
    }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch(Dispatchers.IO) {
            reminderDao.delete(reminder)
            if (reminder.isNotificationSent) cancelNotification(reminder)
        }
    }

    private fun cancelNotification(reminder: Reminder) {
        workManager.cancelUniqueWork(NOTIFICATION_UNIQUE_WORK_NAME_PREFIX + reminder.id)
    }
}

class ReminderDetailsViewModelFactory(
    private val application: BaseApplication,
    private val reminderDao: ReminderDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReminderDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReminderDetailsViewModel(application, reminderDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}