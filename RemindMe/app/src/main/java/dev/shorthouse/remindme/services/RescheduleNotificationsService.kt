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
import dev.shorthouse.remindme.utilities.AlarmHelper
import dev.shorthouse.remindme.utilities.RESCHEDULE_ALARMS_SERVICE_CHANNEL_ID
import dev.shorthouse.remindme.utilities.RESCHEDULE_ALARMS_SERVICE_ID


class RescheduleNotificationsService : Service() {

    override fun onCreate() {
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(RESCHEDULE_ALARMS_SERVICE_ID, getNotification())

        val remindersLiveData =
            ReminderDatabase.getDatabase(this)
                .reminderDao()
                .getNonArchivedReminders()
                .asLiveData()

        remindersLiveData.observeForever(object : Observer<List<Reminder>> {
            override fun onChanged(reminders: List<Reminder>?) {
                if (reminders == null) return
                remindersLiveData.removeObserver(this)
                rescheduleNotifications(reminders)
                stopForeground(true)
                stopSelf()
            }
        })

        return START_NOT_STICKY
    }

    private fun rescheduleNotifications(reminders: List<Reminder>) {
        val alarmHelper = AlarmHelper()
        val notifyingReminders = reminders.filter { reminder -> reminder.isNotificationSent }

        notifyingReminders.forEach { reminder ->
            alarmHelper.setNotificationAlarm(this, reminder)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val appName = getString(R.string.app_name)
            val serviceChannel = NotificationChannel(
                RESCHEDULE_ALARMS_SERVICE_CHANNEL_ID,
                appName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(serviceChannel)
        }
    }

    private fun getNotification(): Notification {
        return NotificationCompat.Builder(this, RESCHEDULE_ALARMS_SERVICE_CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.notification_reschedule_body))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}