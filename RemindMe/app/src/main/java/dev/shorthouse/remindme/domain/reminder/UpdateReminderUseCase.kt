package dev.shorthouse.remindme.domain.reminder

import dev.shorthouse.remindme.data.source.local.ReminderRepository
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.domain.notification.CancelScheduledNotificationUseCase
import dev.shorthouse.remindme.domain.notification.ScheduleNotificationUseCase
import dev.shorthouse.remindme.model.Reminder
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class UpdateReminderUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val scheduleNotificationUseCase: ScheduleNotificationUseCase,
    private val cancelScheduledNotificationUseCase: CancelScheduledNotificationUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(reminder: Reminder) {
        updateReminder(reminder)
    }

    private suspend fun updateReminder(reminder: Reminder) {
        withContext(ioDispatcher) {
            reminderRepository.updateReminder(reminder)

            cancelScheduledNotificationUseCase(reminder)

            if (reminder.isNotificationSent && !reminder.isOverdue && !reminder.isCompleted) {
                scheduleNotificationUseCase(reminder)
            }
        }
    }
}
