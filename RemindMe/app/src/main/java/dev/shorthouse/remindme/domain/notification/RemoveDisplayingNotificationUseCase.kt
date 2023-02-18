package dev.shorthouse.remindme.domain.notification

import androidx.core.app.NotificationManagerCompat
import dev.shorthouse.remindme.model.Reminder
import javax.inject.Inject

class RemoveDisplayingNotificationUseCase @Inject constructor(
    private val notificationManager: NotificationManagerCompat
) {
    operator fun invoke(reminder: Reminder) {
        removeDisplayingNotificationUseCase(reminder)
    }

    private fun removeDisplayingNotificationUseCase(reminder: Reminder) {
        notificationManager.cancel(reminder.id.toInt())
    }
}
