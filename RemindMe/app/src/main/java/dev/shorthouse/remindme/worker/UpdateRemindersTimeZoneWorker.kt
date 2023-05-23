package dev.shorthouse.remindme.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.domain.reminder.GetRemindersUseCase
import dev.shorthouse.remindme.domain.reminder.UpdateReminderUseCase
import java.time.ZoneId
import java.time.ZonedDateTime

private const val MAX_RETRY_ATTEMPTS = 5

@HiltWorker
class UpdateRemindersTimeZoneWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val getRemindersUseCase: GetRemindersUseCase,
    private val updateReminderUseCase: UpdateReminderUseCase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        if (runAttemptCount > MAX_RETRY_ATTEMPTS) {
            return Result.failure()
        }

        val newTimeZone = inputData.getString(context.getString(R.string.key_time_zone))
            ?: return Result.retry()

        return try {
            updateRemindersTimeZone(newTimeZone)
            Result.success()
        } catch (exception: Exception) {
            Result.retry()
        }
    }

    private suspend fun updateRemindersTimeZone(newTimeZone: String) {
        val reminders = getRemindersUseCase()
        val newTimeZoneId = ZoneId.of(newTimeZone)

        reminders.forEach { reminder ->
            val newStartDateTime = ZonedDateTime.of(
                reminder.startDateTime.toLocalDateTime(),
                newTimeZoneId
            )

            val updatedReminder = reminder.copy(
                startDateTime = newStartDateTime
            )

            updateReminderUseCase(updatedReminder)
        }
    }
}