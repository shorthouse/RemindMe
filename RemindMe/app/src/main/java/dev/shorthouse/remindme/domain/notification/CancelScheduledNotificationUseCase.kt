package dev.shorthouse.remindme.domain.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.receiver.DisplayReminderNotificationReceiver
import javax.inject.Inject

class CancelScheduledNotificationUseCase @Inject constructor(
    private val alarmManager: AlarmManager,
    @ApplicationContext private val context: Context
) {
    operator fun invoke(reminder: Reminder) {
        cancelScheduledNotificationUseCase(reminder)
    }

    private fun cancelScheduledNotificationUseCase(reminder: Reminder) {
        val alarmIntent = Intent(context, DisplayReminderNotificationReceiver::class.java)
            .putExtra(context.getString(R.string.intent_key_reminderId), reminder.id)

        val receiverIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.toInt(),
            alarmIntent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        receiverIntent?.let {
            alarmManager.cancel(it)
        }
    }
}
