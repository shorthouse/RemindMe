package dev.shorthouse.remindme.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.data.ReminderDatabase
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.utilities.UPDATED_TIME_ZONE_EXTRA
import dev.shorthouse.remindme.utilities.UPDATE_REMINDER_TIME_ZONE_SERVICE_ID
import dev.shorthouse.remindme.utilities.UPDATE_REMINDER_TIME_ZONE__SERVICE_CHANNEL_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.ZonedDateTime

class UpdateReminderTimeZoneService : Service() {

    override fun onCreate() {
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(UPDATE_REMINDER_TIME_ZONE_SERVICE_ID, getNotification())

        val remindersLiveData =
            ReminderDatabase.getDatabase(this)
                .reminderDao()
                .getReminders()
                .asLiveData()

        val newTimeZone = intent?.getStringExtra(UPDATED_TIME_ZONE_EXTRA) ?: return START_NOT_STICKY

        remindersLiveData.observeForever(object : Observer<List<Reminder>> {
            override fun onChanged(reminders: List<Reminder>?) {
                if (reminders == null) return
                remindersLiveData.removeObserver(this)
                updateReminderTimeZones(reminders, newTimeZone)
                stopForeground(true)
                stopSelf()
            }
        })

        return super.onStartCommand(intent, flags, startId)
    }

    private fun updateReminderTimeZones(reminders: List<Reminder>, newTimeZone: String) {
        val reminderDao = ReminderDatabase
            .getDatabase(this)
            .reminderDao()

        val newZoneId = ZoneId.of(newTimeZone)
        reminders.forEach { reminder ->
            val newReminder = Reminder(
                reminder.id,
                reminder.name,
                ZonedDateTime.of(reminder.startDateTime.toLocalDateTime(), newZoneId),
                reminder.repeatInterval,
                reminder.notes,
                reminder.isArchived,
                reminder.isNotificationSent
            )

            CoroutineScope(Dispatchers.IO).launch {
                reminderDao.update(newReminder)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val appName = getString(R.string.app_name)
            val serviceChannel = NotificationChannel(
                UPDATE_REMINDER_TIME_ZONE__SERVICE_CHANNEL_ID,
                appName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(serviceChannel)
        }
    }

    private fun getNotification(): Notification {
        return NotificationCompat.Builder(this, UPDATE_REMINDER_TIME_ZONE__SERVICE_CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_timezone_change_title))
            .setContentText(getString(R.string.notification_timezone_change_text))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}