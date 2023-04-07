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
import dev.shorthouse.remindme.data.source.local.ReminderRepository
import dev.shorthouse.remindme.di.IoDispatcher
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UpdateReminderTimeZoneService : Service() {

    @Inject
    @IoDispatcher
    lateinit var ioDispatcher: CoroutineDispatcher

    @Inject
    lateinit var repository: ReminderRepository

    override fun onCreate() {
        super.onCreate()
        createServiceNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val newTimeZone = intent?.getStringExtra(this.getString(R.string.intent_key_timeZone))
        if (newTimeZone == null) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }

        startForeground(System.currentTimeMillis().toInt(), getServiceNotification())

        updateReminderTimeZones(newTimeZone)

        return super.onStartCommand(intent, flags, startId)
    }

    private fun updateReminderTimeZones(newTimeZone: String) {
        CoroutineScope(ioDispatcher + SupervisorJob()).launch {
            val reminders = repository.getRemindersOneShot()
            val newTimeZoneId = ZoneId.of(newTimeZone)

            reminders.forEach { reminder ->
                val newStartDateTime = ZonedDateTime.of(
                    reminder.startDateTime.toLocalDateTime(),
                    newTimeZoneId
                )

                val updatedReminder = reminder.copy(
                    startDateTime = newStartDateTime
                )

                repository.updateReminder(updatedReminder)

                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
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
            .setContentText(getString(R.string.notification_body_text_time_zone_update))
            .setSmallIcon(R.drawable.ic_user_notification)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
