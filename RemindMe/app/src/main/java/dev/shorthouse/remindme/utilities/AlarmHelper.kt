package dev.shorthouse.remindme.utilities

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.receivers.AlarmNotificationReceiver

class AlarmHelper {
    fun setNotificationAlarm(
        context: Context,
        reminder: Reminder,
        alarmTriggerAtMillis: Long,
        alarmRepeatIntervalMillis: Long?
    ) {
        val alarmManager = getAlarmManager(context)
        val alarmIntent = getAlarmIntent(context, reminder)
        val alarmBroadcastIntent = getNewBroadcastIntent(context, alarmIntent, reminder)

        if (alarmRepeatIntervalMillis == null) {
            scheduleOneTimeNotification(
                alarmManager,
                alarmTriggerAtMillis,
                alarmBroadcastIntent
            )
        } else {
            scheduleRepeatNotification(
                alarmManager,
                alarmTriggerAtMillis,
                alarmRepeatIntervalMillis,
                alarmBroadcastIntent
            )
        }
    }

    fun cancelExistingNotificationAlarm(
        context: Context,
        reminder: Reminder
    ) {
        val alarmManager = getAlarmManager(context)
        val alarmIntent = getAlarmIntent(context, reminder)
        val alarmBroadcastIntent = getExistingBroadcastIntent(context, alarmIntent, reminder)

        if (alarmBroadcastIntent != null) {
            alarmManager.cancel(alarmBroadcastIntent)
        }
    }

    private fun getAlarmManager(context: Context): AlarmManager {
        return context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    private fun getAlarmIntent(context: Context, reminder: Reminder): Intent {
        return Intent(context, AlarmNotificationReceiver::class.java)
            .putExtra(
                "reminderName",
                reminder.name
            )
    }

    private fun getNewBroadcastIntent(
        context: Context,
        alarmIntent: Intent,
        reminder: Reminder
    ): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            reminder.id.toInt(),
            alarmIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT,
        )
    }

    private fun getExistingBroadcastIntent(
        context: Context,
        alarmIntent: Intent,
        reminder: Reminder
    ): PendingIntent? {
        return PendingIntent.getBroadcast(
            context,
            reminder.id.toInt(),
            alarmIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )
    }

    private fun scheduleOneTimeNotification(
        alarmManager: AlarmManager,
        alarmTriggerAtMillis: Long,
        alarmBroadcastIntent: PendingIntent
    ) {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarmTriggerAtMillis,
            alarmBroadcastIntent
        )
    }

    private fun scheduleRepeatNotification(
        alarmManager: AlarmManager,
        alarmTriggerAtMillis: Long,
        alarmRepeatIntervalMillis: Long,
        alarmBroadcastIntent: PendingIntent
    ) {
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            alarmTriggerAtMillis,
            alarmRepeatIntervalMillis,
            alarmBroadcastIntent
        )
    }
}