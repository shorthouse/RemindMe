package dev.shorthouse.remindme.domain.notification

import dev.shorthouse.remindme.model.Reminder
import javax.inject.Inject

class ScheduleNotificationUseCase @Inject constructor(
    private val scheduleOnetimeNotificationUseCase: ScheduleOnetimeNotificationUseCase,
    private val scheduleRepeatNotificationUseCase: ScheduleRepeatNotificationUseCase
) {
    operator fun invoke(reminder: Reminder) {
        scheduleNotificationUseCase(reminder)
    }

    private fun scheduleNotificationUseCase(reminder: Reminder) {
        if (reminder.isRepeatReminder) {
            scheduleRepeatNotificationUseCase(reminder)
        } else {
            scheduleOnetimeNotificationUseCase(reminder)
        }
    }
}
