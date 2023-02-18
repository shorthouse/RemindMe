package dev.shorthouse.remindme.domain.reminder

import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.domain.notification.RemoveDisplayingNotificationUseCase
import dev.shorthouse.remindme.model.Reminder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class CompleteOnetimeReminderUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val removeDisplayingNotificationUseCase: RemoveDisplayingNotificationUseCase
) {
    private val coroutineScope = CoroutineScope(ioDispatcher)

    operator fun invoke(reminder: Reminder) {
        completeOnetimeReminder(reminder)
    }

    private fun completeOnetimeReminder(reminder: Reminder) {
        coroutineScope.launch {
            reminderRepository.completeReminder(reminder.id)

            removeDisplayingNotificationUseCase(reminder)
        }
    }
}
