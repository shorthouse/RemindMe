package dev.shorthouse.remindme.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.utilities.DATE_INPUT_PATTERN
import dev.shorthouse.remindme.utilities.NotificationScheduler
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val repository: ReminderRepository,
    private val notificationScheduler: NotificationScheduler,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    fun getReminder(id: Long): LiveData<Reminder> {
        return repository.getReminder(id).asLiveData()
    }

    fun editReminder(reminder: Reminder) {
        viewModelScope.launch(ioDispatcher) {
            repository.updateReminder(reminder)
            cancelExistingReminderNotification(reminder)

            if (reminder.isNotificationSent) {
                scheduleReminderNotification(reminder)
            }
        }
    }

    private fun scheduleReminderNotification(reminder: Reminder) {
        notificationScheduler.scheduleReminderNotification(reminder)
    }

    private fun cancelExistingReminderNotification(reminder: Reminder) {
        notificationScheduler.cancelExistingReminderNotification(reminder)
    }
}
