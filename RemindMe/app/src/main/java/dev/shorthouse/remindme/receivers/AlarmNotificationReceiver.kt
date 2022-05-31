package dev.shorthouse.remindme.receivers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dev.shorthouse.remindme.MainActivity
import dev.shorthouse.remindme.R

class AlarmNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        createNotificationChannel(context)
        displayReminderNotification(context, intent)
    }

    private fun displayReminderNotification(
        context: Context,
        intent: Intent,
    ) {
        val reminderNotification = getReminderNotification(context, intent)

        reminderNotification?.let {
            NotificationManagerCompat.from(context).notify(
                intent.getLongExtra(
                    context.getString(R.string.intent_key_reminderId),
                    -1L
                ).toInt(),
                reminderNotification
            )
        }
    }

    private fun getReminderNotification(context: Context, intent: Intent): Notification? {
        val notificationIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val reminderId =
            intent.getLongExtra(context.getString(R.string.intent_key_reminderId), -1L)
        val notificationTitle =
            intent.getStringExtra(context.getString(R.string.intent_key_notificationTitle))
        val notificationText =
            intent.getStringExtra(context.getString(R.string.intent_key_notificationText))
        if (isIntentValuesInvalid(reminderId, notificationTitle, notificationText)) return null

        val pendingIntent = PendingIntent.getActivity(
            context, reminderId.toInt(), notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(
            context,
            context.getString(R.string.notification_channel_id_reminder)
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .addAction(
                R.drawable.ic_clock,
                context.getString(R.string.reminder_notification_action_done_text),
                getDoneActionIntent(context, reminderId)
            )
            .setAutoCancel(true)
            .build()
    }

    private fun getDoneActionIntent(context: Context, reminderId: Long): PendingIntent {
        val doneIntent = Intent(context, NotificationActionDoneReceiver::class.java)
            .putExtra(context.getString(R.string.intent_key_reminderId), reminderId)

        return PendingIntent.getBroadcast(context, reminderId.toInt(), doneIntent, 0)
    }

    private fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            context.getString(R.string.notification_channel_id_reminder),
            context.getString(R.string.notification_channel_name_reminder),
            NotificationManager.IMPORTANCE_HIGH
        )

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        notificationManager?.createNotificationChannel(channel)
    }

    private fun isIntentValuesInvalid(
        reminderId: Long,
        notificationTitle: String?,
        notificationText: String?
    ): Boolean {
        return reminderId == -1L || notificationTitle == null || notificationText == null
    }
}
