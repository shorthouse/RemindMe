package dev.shorthouse.remindme.utilities

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.receivers.AlarmNotificationReceiver

class AlarmHelper {
    fun setAlarm(
        context: Context,
        reminder: Reminder,
        alarmTriggerAtMillis: Long,
        alarmRepeatIntervalMillis: Long?
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val alarmIntent = Intent(context, AlarmNotificationReceiver::class.java)
        alarmIntent.putExtra("reminderName", reminder.name)

        val alarmBroadcastIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.toInt(),
            alarmIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT,
        )

        if (alarmRepeatIntervalMillis == null) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                alarmTriggerAtMillis,
                alarmBroadcastIntent
            )
        } else {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                alarmTriggerAtMillis,
                61000,
                alarmBroadcastIntent
            )
        }
    }

    fun cancelAlarm(
        context: Context,
        reminder: Reminder
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val alarmIntent = Intent(context, AlarmNotificationReceiver::class.java)
        alarmIntent.putExtra("reminderName", reminder.name)

        val alarmBroadcastIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.toInt(),
            alarmIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE,
        )

        if (alarmBroadcastIntent != null) {
            alarmManager.cancel(alarmBroadcastIntent)
        }
    }
}