package dev.shorthouse.remindme.worker

import android.content.Context
import android.util.Log
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
        Log.d("HDS", "got to doWork()")
        if (runAttemptCount > MAX_RETRY_ATTEMPTS) {
            Log.d("HDS", "max attempts reached")

            return Result.failure()
        }

        return try {
            rescheduleReminderNotifications()
            Log.d("HDS", "success!")

            Result.success()
        } catch (exception: Exception) {
            Log.d("HDS", "error in catch")

            Result.retry()
        }
    }

    private suspend fun rescheduleReminderNotifications() {
        Log.d("HDS", "rescheduling notifs..")

        val reminders = getRemindersUseCase()

        reminders.filter { it.isNotificationSent && !it.isCompleted && !it.isOverdue }
            .forEach { scheduleNotificationUseCase(it) }
    }
}
