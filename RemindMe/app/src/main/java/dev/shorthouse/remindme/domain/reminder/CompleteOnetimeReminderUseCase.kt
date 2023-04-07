package dev.shorthouse.remindme.domain.reminder

import dev.shorthouse.remindme.data.source.local.ReminderRepository
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.domain.notification.CancelScheduledNotificationUseCase
import dev.shorthouse.remindme.domain.notification.RemoveDisplayingNotificationUseCase
import dev.shorthouse.remindme.model.Reminder
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class CompleteOnetimeReminderUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val cancelScheduledNotificationUseCase: CancelScheduledNotificationUseCase,
    private val removeDisplayingNotificationUseCase: RemoveDisplayingNotificationUseCase
) {
    private val coroutineScope = CoroutineScope(ioDispatcher)

    operator fun invoke(reminder: Reminder) {
        completeOnetimeReminder(reminder)
    }

    private fun completeOnetimeReminder(reminder: Reminder) {
        coroutineScope.launch {
            reminderRepository.completeReminder(reminder.id)

            cancelScheduledNotificationUseCase(reminder)
            removeDisplayingNotificationUseCase(reminder)
        }
    }
}
