package dev.shorthouse.remindme.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.util.NotificationScheduler
import dev.shorthouse.remindme.util.UiText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class InputViewModel @Inject constructor(
    private val repository: ReminderRepository,
    private val notificationScheduler: NotificationScheduler,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {
    fun getReminder(reminderId: Long): LiveData<Reminder> {
        return repository.getReminder(reminderId).asLiveData()
    }

    fun isReminderValid(reminder: Reminder): Boolean {
        return reminder.name.isNotBlank() &&
                reminder.startDateTime.isAfter(ZonedDateTime.now())
    }

    fun getErrorMessage(reminder: Reminder): UiText.StringResource {
        return when {
            reminder.name.isBlank() -> UiText.StringResource(R.string.error_name_empty)
            else -> UiText.StringResource(R.string.error_time_past)
        }
    }

    fun saveReminder(reminder: Reminder) {
        when (reminder.id) {
            0L -> addReminder(reminder)
            else -> editReminder(reminder)
        }
    }

    private fun addReminder(reminder: Reminder) {
        viewModelScope.launch(ioDispatcher) {
            val reminderId = repository.insertReminder(reminder)

            if (reminder.isNotificationSent) {
                reminder.id = reminderId
                notificationScheduler.scheduleReminderNotification(reminder)
            }
        }
    }

    private fun editReminder(reminder: Reminder) {
        viewModelScope.launch(ioDispatcher) {
            repository.updateReminder(reminder)
            notificationScheduler.cancelScheduledReminderNotification(reminder)

            if (reminder.isNotificationSent) {
                notificationScheduler.scheduleReminderNotification(reminder)
            }
        }
    }
}
