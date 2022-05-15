package dev.shorthouse.remindme.receivers

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
import dev.shorthouse.remindme.utilities.REMINDER_NOTIFICATION_ID

class AlarmNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        context?.let { makeReminderNotification(it, intent) }
    }

    private fun makeReminderNotification(context: Context, intent: Intent) {
        val name = context.getString(R.string.notification_reminder_name)
        val description = context.getString(R.string.notification_reminder_description)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(
            context.getString(R.string.notification_channel_id),
            name,
            importance
        )
        channel.description = description

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        notificationManager?.createNotificationChannel(channel)

        val reminderName = intent.getStringExtra("reminderName")

        val notificationIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(
                context,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
            )

        val builder =
            NotificationCompat.Builder(context, context.getString(R.string.notification_channel_id))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(context.getString(R.string.notification_content_title))
                .setContentText(reminderName)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        NotificationManagerCompat.from(context).notify(REMINDER_NOTIFICATION_ID, builder.build())
    }

}