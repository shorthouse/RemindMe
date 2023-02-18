package dev.shorthouse.remindme.domain

import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.util.NotificationScheduler
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class EditReminderUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val notificationScheduler: NotificationScheduler,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {
    private val coroutineScope = CoroutineScope(ioDispatcher)

    operator fun invoke(reminder: Reminder) {
        editReminder(reminder)
    }

    private fun editReminder(reminder: Reminder) {
        coroutineScope.launch {
            reminderRepository.updateReminder(reminder)

            notificationScheduler.cancelScheduledReminderNotification(reminder)
            notificationScheduler.removeDisplayingReminderNotification(reminder)

            if (reminder.isNotificationSent) {
                notificationScheduler.scheduleReminderNotification(reminder)
            }
        }
    }
}
