package dev.shorthouse.remindme.domain.reminder

import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.domain.notification.ScheduleNotificationUseCase
import dev.shorthouse.remindme.model.Reminder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddReminderUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val scheduleReminderNotificationUseCase: ScheduleNotificationUseCase
) {
    private val coroutineScope = CoroutineScope(ioDispatcher)

    operator fun invoke(reminder: Reminder) {
        addReminder(reminder)
    }

    private fun addReminder(reminder: Reminder) {
        coroutineScope.launch {
            val addedReminderId = reminderRepository.insertReminder(reminder)

            if (reminder.isNotificationSent) {
                val addedReminder = reminder.copy(id = addedReminderId)
                scheduleReminderNotificationUseCase(addedReminder)
            }
        }
    }
}
