package dev.shorthouse.remindme.domain.reminder

import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.domain.notification.CancelScheduledNotificationUseCase
import dev.shorthouse.remindme.domain.notification.RemoveDisplayingNotificationUseCase
import dev.shorthouse.remindme.model.Reminder
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class CompleteRepeatReminderSeriesUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val cancelScheduledNotificationUseCase: CancelScheduledNotificationUseCase,
    private val removeDisplayingNotificationUseCase: RemoveDisplayingNotificationUseCase
) {
    private val coroutineScope = CoroutineScope(ioDispatcher)

    operator fun invoke(reminder: Reminder) {
        completeRepeatReminderSeries(reminder)
    }

    private fun completeRepeatReminderSeries(reminder: Reminder) {
        coroutineScope.launch(ioDispatcher) {
            reminderRepository.completeReminder(reminder.id)

            cancelScheduledNotificationUseCase(reminder)
            removeDisplayingNotificationUseCase(reminder)
        }
    }
}
