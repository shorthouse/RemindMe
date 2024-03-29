package dev.shorthouse.remindme.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.shorthouse.remindme.domain.notification.ScheduleNotificationUseCase
import dev.shorthouse.remindme.domain.reminder.GetRemindersUseCase

private const val MAX_RETRY_ATTEMPTS = 5

@HiltWorker
class RescheduleReminderNotificationsWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val getRemindersUseCase: GetRemindersUseCase,
    private val scheduleNotificationUseCase: ScheduleNotificationUseCase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        if (runAttemptCount > MAX_RETRY_ATTEMPTS) {
            return Result.failure()
        }

        return try {
            rescheduleReminderNotifications()
            Result.success()
        } catch (exception: Exception) {
            Result.retry()
        }
    }

    private suspend fun rescheduleReminderNotifications() {
        val reminders = getRemindersUseCase()

        reminders.filter { it.isNotificationSent && !it.isOverdue && !it.isCompleted }
            .forEach { scheduleNotificationUseCase(it) }
    }
}
