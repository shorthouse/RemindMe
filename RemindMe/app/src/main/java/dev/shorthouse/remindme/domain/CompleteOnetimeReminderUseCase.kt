package dev.shorthouse.remindme.domain

import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.util.NotificationScheduler
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class CompleteOnetimeReminderUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val notificationScheduler: NotificationScheduler,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {
    private val coroutineScope = CoroutineScope(ioDispatcher)

    operator fun invoke(reminder: Reminder) {
        completeOnetimeReminder(reminder)
    }

    private fun completeOnetimeReminder(reminder: Reminder) {
        notificationScheduler.removeDisplayingReminderNotification(reminder)

        coroutineScope.launch {
            reminderRepository.completeReminder(reminder.id)
        }
    }
}
