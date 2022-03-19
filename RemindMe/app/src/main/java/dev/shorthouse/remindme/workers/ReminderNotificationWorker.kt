package dev.shorthouse.remindme.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.utilities.KEY_REMINDER_NAME
import dev.shorthouse.remindme.utilities.REMINDER_NOTIFICATION_ID


class ReminderNotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        makeReminderNotification(applicationContext)
        return Result.success()
    }

    private fun makeReminderNotification(context: Context) {
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

        val reminderName = inputData.getString(KEY_REMINDER_NAME)
        val builder =
            NotificationCompat.Builder(context, context.getString(R.string.notification_channel_id))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(context.getString(R.string.notification_content_title))
                .setContentText(reminderName)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

        NotificationManagerCompat.from(context).notify(REMINDER_NOTIFICATION_ID, builder.build())
    }
}
