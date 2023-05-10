package dev.shorthouse.remindme.domain.reminder

import dev.shorthouse.remindme.data.source.local.ReminderRepository
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.domain.notification.ScheduleNotificationUseCase
import dev.shorthouse.remindme.model.Reminder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddReminderUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val scheduleReminderNotificationUseCase: ScheduleNotificationUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(reminder: Reminder) {
        addReminder(reminder)
    }

    private suspend fun addReminder(reminder: Reminder) {
        withContext(ioDispatcher) {
            val addedReminderId = reminderRepository.insertReminder(reminder)

            if (reminder.isNotificationSent) {
                val addedReminder = reminder.copy(id = addedReminderId)
                scheduleReminderNotificationUseCase(addedReminder)
            }
        }
    }
}
