package dev.shorthouse.remindme.receiver

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.AndroidEntryPoint
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.data.source.local.ReminderRepository
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.ui.MainActivity
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DisplayReminderNotificationReceiver : BroadcastReceiver() {

    @Inject
    @IoDispatcher
    lateinit var ioDispatcher: CoroutineDispatcher

    @Inject
    lateinit var reminderRepository: ReminderRepository

    @Inject
    lateinit var notificationManager: NotificationManagerCompat

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        displayReminderNotification(context, intent)
    }

    private fun displayReminderNotification(context: Context, intent: Intent) {
        notificationManager.createNotificationChannel(
            NotificationChannel(
                context.getString(R.string.notification_channel_id_reminder),
                context.getString(R.string.notification_channel_name_reminder),
                NotificationManager.IMPORTANCE_HIGH
            )
        )

        val reminderId = intent.getLongExtra(context.getString(R.string.intent_key_reminderId), -1L)
        if (reminderId == -1L) return

        CoroutineScope(ioDispatcher).launch {
            val reminder = reminderRepository.getReminderOneShot(reminderId)

            val reminderNotification = createReminderNotification(reminder, context)

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED
            ) {
                notificationManager.notify(reminderId.toInt(), reminderNotification)
            }
        }
    }

    private fun createReminderNotification(reminder: Reminder, context: Context): Notification {
        return NotificationCompat.Builder(
            context,
            context.getString(R.string.notification_channel_id_reminder)
        ).apply {
            setSmallIcon(R.drawable.ic_user_notification)
            setContentTitle(reminder.name)
            setContentText(reminder.getFormattedTime())
            setContentIntent(
                PendingIntent.getActivity(
                    context,
                    reminder.id.toInt(),
                    Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    },
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
            setAutoCancel(true)
            addAction(
                R.drawable.ic_user_notification_action,
                context.getString(R.string.reminder_notification_action_complete_text),
                PendingIntent.getBroadcast(
                    context,
                    reminder.id.toInt(),
                    Intent(context, NotificationActionDoneReceiver::class.java)
                        .putExtra(context.getString(R.string.intent_key_reminderId), reminder.id),
                    FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
                )
            )
            priority = NotificationCompat.PRIORITY_HIGH
        }.build()
    }
}
