package dev.shorthouse.remindme.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import dagger.hilt.android.AndroidEntryPoint
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.model.Reminder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

@AndroidEntryPoint
class UpdateReminderTimeZoneService @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : Service() {

    @Inject
    lateinit var repository: ReminderRepository

    override fun onCreate() {
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

        val remindersLiveData = repository.getReminders().asLiveData()

        remindersLiveData.observeForever(object : Observer<List<Reminder>> {
            override fun onChanged(reminders: List<Reminder>?) {
                reminders?.let {
                    remindersLiveData.removeObserver(this)
                    updateReminderTimeZones(reminders, newTimeZone)
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }
            }
        })

        return super.onStartCommand(intent, flags, startId)
    }

    private fun updateReminderTimeZones(reminders: List<Reminder>, newTimeZone: String) {
        val newZoneId = ZoneId.of(newTimeZone)

        reminders.forEach { reminder ->
            val newReminder = Reminder(
                reminder.id,
                reminder.name,
                ZonedDateTime.of(reminder.startDateTime.toLocalDateTime(), newZoneId),
                reminder.repeatInterval,
                reminder.notes,
                reminder.isComplete,
                reminder.isNotificationSent
            )

            CoroutineScope(ioDispatcher).launch {
                repository.updateReminder(newReminder)
            }
        }
    }

    private fun createServiceNotificationChannel() {
        val notificationChannel = NotificationChannel(
            getString(R.string.notification_channel_id_time_zone_update),
            getString(R.string.notification_channel_name_time_zone_update),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun getServiceNotification(): Notification {
        return NotificationCompat.Builder(
            this,
            getString(R.string.notification_channel_id_time_zone_update)
        )
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.notification_body_text_time_zone_update))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
