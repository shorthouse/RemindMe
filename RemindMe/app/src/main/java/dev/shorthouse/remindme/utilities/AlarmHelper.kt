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

class AlarmHelper {
    fun setNotificationAlarm(
        context: Context,
        reminder: Reminder,
    ) {
        if (reminder.repeatInterval == null) {
            scheduleOneTimeNotification(context, reminder)
        } else {
            scheduleRepeatNotification(context, reminder)
        }
    }

    fun cancelExistingNotificationAlarm(
        context: Context,
        reminder: Reminder
    ) {
        val alarmBroadcastIntent = getExistingBroadcastIntent(context, reminder)

        if (alarmBroadcastIntent != null) {
            getAlarmManager(context).cancel(alarmBroadcastIntent)
        }
    }

    private fun scheduleOneTimeNotification(
        context: Context,
        reminder: Reminder
    ) {
        getAlarmManager(context).setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            getTriggerAtMillis(reminder),
            getNewBroadcastIntent(context, reminder)
        )
    }

    private fun scheduleRepeatNotification(
        context: Context,
        reminder: Reminder
    ) {
        getAlarmManager(context).setRepeating(
            AlarmManager.RTC_WAKEUP,
            getTriggerAtMillis(reminder),
            getRepeatIntervalMillis(reminder),
            getNewBroadcastIntent(context, reminder)
        )
    }

    private fun getNewBroadcastIntent(
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
            .putExtra("reminderId", reminder.id)
            .putExtra("notificationTitle", getReminderNotificationTitle(reminder))
            .putExtra("notificationText", getReminderNotificationText(context, reminder))
    }

    private fun getTriggerAtMillis(reminder: Reminder): Long {
        return reminder.startDateTime.toInstant().toEpochMilli()
    }

    private fun getRepeatIntervalMillis(reminder: Reminder): Long {
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