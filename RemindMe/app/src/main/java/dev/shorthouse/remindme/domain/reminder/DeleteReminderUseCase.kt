package dev.shorthouse.remindme.domain.reminder

import dev.shorthouse.remindme.data.source.local.ReminderRepository
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.domain.notification.CancelScheduledNotificationUseCase
import dev.shorthouse.remindme.model.Reminder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeleteReminderUseCase@Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val cancelScheduledNotificationUseCase: CancelScheduledNotificationUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(reminder: Reminder) {
        deleteReminder(reminder)
    }

    private suspend fun deleteReminder(reminder: Reminder) {
        withContext(ioDispatcher) {
            reminderRepository.deleteReminder(reminder)

            cancelScheduledNotificationUseCase(reminder)
        }
    }
}
