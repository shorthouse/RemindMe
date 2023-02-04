package dev.shorthouse.remindme.utilities

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.model.RepeatInterval
import dev.shorthouse.remindme.receivers.DisplayReminderNotificationReceiver
import java.time.Duration
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class NotificationScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val alarmManager: AlarmManager
) {
    fun scheduleReminderNotification(reminder: Reminder) {
        if (reminder.repeatInterval == null) {
            scheduleOneTimeNotification(reminder)
        } else {
            scheduleRepeatNotification(reminder, reminder.repeatInterval)
        }
    }

    fun cancelScheduledReminderNotification(reminder: Reminder) {
        val alarmBroadcastIntent = getExistingBroadcastIntent(reminder)

        alarmBroadcastIntent?.let {
            alarmManager.cancel(alarmBroadcastIntent)
        }
    }

    fun removeDisplayingNotification(notificationId: Int) {
        NotificationManagerCompat.from(context).cancel(notificationId)
    }

    private fun scheduleOneTimeNotification(reminder: Reminder) {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            getAlarmTriggerTime(reminder),
            getNotificationBroadcastIntent(reminder)
        )
    }

    private fun scheduleRepeatNotification(reminder: Reminder, repeatInterval: RepeatInterval) {
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            getAlarmTriggerTime(reminder),
            getAlarmRepeatInterval(repeatInterval),
            getNotificationBroadcastIntent(reminder)
        )
    }

    private fun getAlarmTriggerTime(reminder: Reminder): Long {
        return reminder.startDateTime.toInstant().toEpochMilli()
    }

    private fun getAlarmRepeatInterval(repeatInterval: RepeatInterval): Long {
        val repeatIntervalDays = when (repeatInterval.unit) {
            ChronoUnit.DAYS -> repeatInterval.amount
            else -> repeatInterval.amount * DAYS_IN_WEEK
        }

        return Duration.ofDays(repeatIntervalDays).toMillis()
    }

    private fun getNotificationBroadcastIntent(reminder: Reminder): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            reminder.id.toInt(),
            getAlarmIntent(reminder),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT,
        )
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

    private fun getExistingBroadcastIntent(reminder: Reminder): PendingIntent? {
        return PendingIntent.getBroadcast(
            context,
            reminder.id.toInt(),
            getAlarmIntent(reminder),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )
    }
}
