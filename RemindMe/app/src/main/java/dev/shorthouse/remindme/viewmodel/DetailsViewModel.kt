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
import dev.shorthouse.remindme.model.RepeatInterval
import dev.shorthouse.remindme.utilities.NotificationScheduler
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: ReminderRepository,
    private val notificationScheduler: NotificationScheduler,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    fun getReminderDetails(reminderId: Long): LiveData<Reminder> {
        return repository.getReminder(reminderId).asLiveData()
    }

    fun deleteReminder(reminderToDelete: Reminder) {
        if (reminderToDelete.isNotificationSent) {
            cancelReminderNotification(reminderToDelete)
        }

        viewModelScope.launch(ioDispatcher) {
            repository.deleteReminder(reminderToDelete)
        }
    }

    fun completeReminder(reminderToComplete: Reminder) {
        if (reminderToComplete.isNotificationSent) {
            cancelReminderNotification(reminderToComplete)
        }

        viewModelScope.launch(ioDispatcher) {
            repository.completeReminder(reminderToComplete.id)
        }
    }

    fun getRepeatIntervalStringId(repeatInterval: RepeatInterval?): Int? {
        if (repeatInterval == null) return null

        return when (repeatInterval.unit) {
            ChronoUnit.DAYS -> R.plurals.interval_days
            else -> R.plurals.interval_weeks
        }
    }

    private fun cancelReminderNotification(reminder: Reminder) {
        notificationScheduler.cancelExistingReminderNotification(reminder)
    }
}
