package dev.shorthouse.remindme.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.receiver.DisplayReminderNotificationReceiver
import javax.inject.Inject

class NotificationScheduler @Inject constructor(
    private val alarmManager: AlarmManager,
    @ApplicationContext private val context: Context
) {
    fun scheduleReminderNotification(reminder: Reminder) {
        val alarmType = AlarmManager.RTC_WAKEUP

        val triggerAtMillis = reminder.startDateTime.toInstant().toEpochMilli()

        val notificationBroadcastIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.toInt(),
            getAlarmIntent(reminder),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT,
        )

        if (reminder.repeatInterval != null) {
            val repeatIntervalMillis = reminder.repeatInterval.unit.duration
                .multipliedBy(reminder.repeatInterval.amount)
                .toMillis()

            alarmManager.setRepeating(
                alarmType,
                triggerAtMillis,
                repeatIntervalMillis,
                notificationBroadcastIntent
            )
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                alarmType,
                triggerAtMillis,
                notificationBroadcastIntent
            )
        }
    }

    fun cancelScheduledReminderNotification(reminder: Reminder) {
        val scheduledBroadcastIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.toInt(),
            getAlarmIntent(reminder),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )

        alarmManager.cancel(scheduledBroadcastIntent)
    }

    fun removeDisplayingReminderNotification(reminder: Reminder) {
        val notificationId = reminder.id.toInt()

        NotificationManagerCompat.from(context).cancel(notificationId)
    }

    private fun getAlarmIntent(reminder: Reminder): Intent {
        return Intent(context, DisplayReminderNotificationReceiver::class.java)
            .putExtra(
                context.getString(R.string.intent_key_reminderId),
                reminder.id
            )
            .putExtra(
                context.getString(R.string.intent_key_notificationTitle),
                reminder.name
            )
            .putExtra(
                context.getString(R.string.intent_key_notificationText),
                reminder.getFormattedStartTime()
            )
    }
}
