package dev.shorthouse.remindme.utilities

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.receivers.AlarmNotificationReceiver
import java.time.Duration
import java.time.temporal.ChronoUnit

class NotificationScheduler {
    fun scheduleReminderNotification(context: Context, reminder: Reminder) {
        if (reminder.isRepeatReminder()) {
            scheduleRepeatNotification(context, reminder)
        } else {
            scheduleOneTimeNotification(context, reminder)
        }
    }

    private fun scheduleOneTimeNotification(context: Context, reminder: Reminder) {
        getAlarmManager(context).setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            getAlarmTriggerTime(reminder),
            getNotificationBroadcastIntent(context, reminder)
        )
    }

    private fun scheduleRepeatNotification(context: Context, reminder: Reminder) {
        getAlarmManager(context).setRepeating(
            AlarmManager.RTC_WAKEUP,
            getAlarmTriggerTime(reminder),
            getAlarmRepeatInterval(reminder),
            getNotificationBroadcastIntent(context, reminder)
        )
    }

    private fun getNotificationBroadcastIntent(
        context: Context,
        reminder: Reminder
    ): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            reminder.id.toInt(),
            getAlarmIntent(context, reminder),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT,
        )
    }

    fun cancelExistingReminderNotification(context: Context, reminder: Reminder) {
        val alarmBroadcastIntent = getExistingBroadcastIntent(context, reminder)

        alarmBroadcastIntent?.let {
            getAlarmManager(context).cancel(alarmBroadcastIntent)
        }
    }

    private fun getExistingBroadcastIntent(
        context: Context,
        reminder: Reminder
    ): PendingIntent? {
        return PendingIntent.getBroadcast(
            context,
            reminder.id.toInt(),
            getAlarmIntent(context, reminder),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )
    }

    private fun getAlarmManager(context: Context): AlarmManager {
        return context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    private fun getAlarmIntent(context: Context, reminder: Reminder): Intent {
        return Intent(context, AlarmNotificationReceiver::class.java)
            .putExtra(
                context.getString(R.string.intent_key_reminderId),
                reminder.id
            )
            .putExtra(
                context.getString(R.string.intent_key_notificationTitle),
                getReminderNotificationTitle(reminder)
            )
            .putExtra(
                context.getString(R.string.intent_key_notificationText),
                getReminderNotificationText(context, reminder)
            )
    }

    private fun getAlarmTriggerTime(reminder: Reminder): Long {
        return reminder.startDateTime.toInstant().toEpochMilli()
    }

    private fun getAlarmRepeatInterval(reminder: Reminder): Long {
        val repeatInterval = reminder.repeatInterval!!

        val repeatIntervalDays = when (repeatInterval.timeUnit) {
            ChronoUnit.DAYS -> repeatInterval.timeValue
            else -> repeatInterval.timeValue * DAYS_IN_WEEK
        }

        return Duration.ofDays(repeatIntervalDays).toMillis()
    }

    private fun getReminderNotificationTitle(reminder: Reminder): String {
        return reminder.name
    }

    private fun getReminderNotificationText(context: Context, reminder: Reminder): String {
        val formattedStartTime = reminder.startDateTime.toLocalTime().toString()

        return context.getString(
            R.string.reminder_notification_body,
            formattedStartTime
        )
    }
}
