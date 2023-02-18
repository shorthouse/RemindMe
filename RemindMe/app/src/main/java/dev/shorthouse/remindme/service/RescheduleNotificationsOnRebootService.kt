package dev.shorthouse.remindme.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.util.NotificationScheduler
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RescheduleNotificationsOnRebootService : Service() {

    @Inject
    @IoDispatcher
    lateinit var ioDispatcher: CoroutineDispatcher

    @Inject
    lateinit var repository: ReminderRepository

    @Inject
    lateinit var notificationScheduler: NotificationScheduler

    override fun onCreate() {
        super.onCreate()
        createServiceNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(System.currentTimeMillis().toInt(), getServiceNotification())

        rescheduleNotifications()

        return super.onStartCommand(intent, flags, startId)
    }

    private fun rescheduleNotifications() {
        CoroutineScope(ioDispatcher + SupervisorJob()).launch {
            val reminders = repository.getRemindersOneShot()

            reminders
                .filter { reminder -> reminder.isNotificationSent }
                .forEach { reminder -> notificationScheduler.scheduleReminderNotification(reminder) }

            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    private fun createServiceNotificationChannel() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val notificationChannel = NotificationChannel(
            getString(R.string.notification_channel_id_reminder),
            getString(R.string.notification_channel_name_reminder),
            NotificationManager.IMPORTANCE_DEFAULT
        )

        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun getServiceNotification(): Notification {
        return NotificationCompat.Builder(
            this,
            getString(R.string.notification_channel_id_reminder)
        )
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.notification_body_text_reboot_reschedule))
            .setSmallIcon(R.drawable.ic_user_notification)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
