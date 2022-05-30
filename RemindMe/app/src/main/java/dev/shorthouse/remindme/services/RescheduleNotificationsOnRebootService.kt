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
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.utilities.NotificationScheduler
import dev.shorthouse.remindme.utilities.RESCHEDULE_NOTIFICATIONS_SERVICE_ID
import javax.inject.Inject

@AndroidEntryPoint
class RescheduleNotificationsOnRebootService @Inject constructor(
    private val repository: ReminderRepository,
    private val notificationScheduler: NotificationScheduler,
) : Service() {
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "RESCHEDULE_ALARMS_SERVICE_CHANNEL_ID"
    }

    override fun onCreate() {
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(RESCHEDULE_NOTIFICATIONS_SERVICE_ID, getNotification())

        val remindersLiveData = repository.getNonArchivedReminders().asLiveData()

        remindersLiveData.observeForever(object : Observer<List<Reminder>> {
            override fun onChanged(reminders: List<Reminder>?) {
                reminders?.let {
                    remindersLiveData.removeObserver(this)
                    rescheduleNotifications(reminders)
                    stopForeground(true)
                    stopSelf()
                }
            }
        })

        return super.onStartCommand(intent, flags, startId)
    }

    private fun rescheduleNotifications(nonArchivedReminders: List<Reminder>) {
        nonArchivedReminders
            .filter { reminder -> reminder.isNotificationSent }
            .forEach { reminder ->
                notificationScheduler.scheduleReminderNotification(reminder)
            }
    }

    private fun createNotificationChannel() {
        val appName = getString(R.string.app_name)
        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            appName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun getNotification(): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.notification_reschedule_body))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
