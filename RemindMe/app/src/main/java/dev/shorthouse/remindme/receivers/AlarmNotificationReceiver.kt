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
    companion object {
        const val REMINDER_ID = "reminderId"
        const val NOTIFICATION_TITLE = "notificationTitle"
        const val NOTIFICATION_TEXT = "notificationText"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        createNotificationChannel(context)
        val reminderNotification = getReminderNotification(context, intent)
        if (reminderNotification != null) displayReminderNotification(
            context,
            intent,
            reminderNotification
        )
    }

    private fun getReminderNotification(context: Context, intent: Intent): Notification? {
        val notificationIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val reminderId = intent.getLongExtra(REMINDER_ID, -1L)
        val notificationTitle = intent.getStringExtra(NOTIFICATION_TITLE)
        val notificationText = intent.getStringExtra(NOTIFICATION_TEXT)
        if (reminderId == -1L || notificationTitle == null || notificationText == null) return null

        return NotificationCompat.Builder(
            context,
            context.getString(R.string.notification_channel_id)
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_clock, "Done", getDoneActionIntent(context, reminderId))
            .setAutoCancel(true)
            .build()
    }

    private fun displayReminderNotification(
        context: Context,
        intent: Intent,
        reminderNotification: Notification
    ) {
        NotificationManagerCompat.from(context)
            .notify(intent.getLongExtra(REMINDER_ID, -1L).toInt(), reminderNotification)
    }

    private fun getDoneActionIntent(context: Context, reminderId: Long): PendingIntent {
        val doneIntent = Intent(context, NotificationActionDoneReceiver::class.java)
            .putExtra(REMINDER_ID, reminderId)

        return PendingIntent.getBroadcast(context, reminderId.toInt(), doneIntent, 0)
    }

    private fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            context.getString(R.string.notification_channel_id),
            context.getString(R.string.notification_reminder_name),
            NotificationManager.IMPORTANCE_HIGH
        )

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        notificationManager?.createNotificationChannel(channel)
    }
}
