package dev.shorthouse.remindme.domain.reminder

import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.util.NotificationScheduler
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddReminderUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val notificationScheduler: NotificationScheduler,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {
    private val coroutineScope = CoroutineScope(ioDispatcher)

    operator fun invoke(reminder: Reminder) {
        addReminder(reminder)
    }

    private fun addReminder(reminder: Reminder) {
        coroutineScope.launch {
            val reminderId = reminderRepository.insertReminder(reminder)

            if (reminder.isNotificationSent) {
                val addedReminder = reminder.copy(id = reminderId)
                notificationScheduler.scheduleReminderNotification(addedReminder)
            }
        }
    }
}
