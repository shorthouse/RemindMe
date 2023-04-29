package dev.shorthouse.remindme.domain.reminder

import dev.shorthouse.remindme.data.source.local.ReminderRepository
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.domain.notification.CancelScheduledNotificationUseCase
import dev.shorthouse.remindme.domain.notification.RemoveDisplayingNotificationUseCase
import dev.shorthouse.remindme.domain.notification.ScheduleNotificationUseCase
import dev.shorthouse.remindme.model.Reminder
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class UpdateReminderUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val scheduleNotificationUseCase: ScheduleNotificationUseCase,
    private val cancelScheduledNotificationUseCase: CancelScheduledNotificationUseCase,
    private val removeDisplayingNotificationUseCase: RemoveDisplayingNotificationUseCase
) {
    private val coroutineScope = CoroutineScope(ioDispatcher)

    operator fun invoke(reminder: Reminder) {
        updateReminder(reminder)
    }

    private fun updateReminder(reminder: Reminder) {
        coroutineScope.launch {
            reminderRepository.updateReminder(reminder)

            cancelScheduledNotificationUseCase(reminder)
            removeDisplayingNotificationUseCase(reminder)

            if (reminder.isNotificationSent) {
                scheduleNotificationUseCase(reminder)
            }
        }
    }
}
