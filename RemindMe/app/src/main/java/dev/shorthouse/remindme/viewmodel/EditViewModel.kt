package dev.shorthouse.remindme.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.utilities.NotificationScheduler
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

class EditViewModel @Inject constructor(
    private val repository: ReminderRepository,
    private val notificationScheduler: NotificationScheduler,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    state: SavedStateHandle
) : ViewModel() {
    private val reminderId = state.get<Long>("id") ?: 1L
    val reminder = repository.getReminder(reminderId).asLiveData()

    fun editReminder(reminder: Reminder) {
        viewModelScope.launch(ioDispatcher) {
            repository.updateReminder(reminder)
            notificationScheduler.cancelExistingReminderNotification(reminder)

            if (reminder.isNotificationSent) {
                notificationScheduler.scheduleReminderNotification(reminder)
            }
        }
    }
}
