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

class ScheduleRepeatNotificationUseCase @Inject constructor(
    private val alarmManager: AlarmManager,
    @ApplicationContext private val context: Context
) {
    operator fun invoke(reminder: Reminder) {
        scheduleRepeatReminderNotification(reminder)
    }

    private fun scheduleRepeatReminderNotification(reminder: Reminder) {
        reminder.repeatInterval?.let {
            val alarmIntent = Intent(context, DisplayReminderNotificationReceiver::class.java)
                .putExtra(context.getString(R.string.intent_key_reminderId), reminder.id)

            val receiverIntent = PendingIntent.getBroadcast(
                context,
                reminder.id.toInt(),
                alarmIntent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                reminder.startDateTime.toInstant().toEpochMilli(),
                reminder.repeatInterval.unit.duration
                    .multipliedBy(reminder.repeatInterval.amount)
                    .toMillis(),
                receiverIntent
            )
        }
    }
}
