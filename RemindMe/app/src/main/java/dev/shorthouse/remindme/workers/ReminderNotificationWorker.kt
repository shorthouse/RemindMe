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
        val name = "Verbose WorkManager Notifications"
        val description = "Verbose WorkManager Notifications"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("VERBOSE_NOTIFICATION", name, importance)
        channel.description = description

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        notificationManager?.createNotificationChannel(channel)

        val reminderName = inputData.getString(KEY_REMINDER_NAME)
        val builder = NotificationCompat.Builder(context, "VERBOSE_NOTIFICATION")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("RemindMe to...")
            .setContentText(reminderName)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        NotificationManagerCompat.from(context).notify(REMINDER_NOTIFICATION_ID, builder.build())
    }
}
