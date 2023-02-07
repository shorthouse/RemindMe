package dev.shorthouse.remindme.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RescheduleNotificationsOnRebootService : Service() {
//
//    @Inject
//    lateinit var repository: ReminderRepository
//
//    @Inject
//    lateinit var notificationScheduler: NotificationScheduler
//
//    override fun onCreate() {
//        super.onCreate()
//        createServiceNotificationChannel()
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        startForeground(System.currentTimeMillis().toInt(), getServiceNotification())
//
//        val remindersLiveData = repository.getReminders().asLiveData()
//
//        remindersLiveData.observeForever(object : Observer<List<Reminder>> {
//            override fun onChanged(reminders: List<Reminder>?) {
//                reminders?.let {
//                    remindersLiveData.removeObserver(this)
//                    rescheduleNotifications(reminders)
//                    stopForeground(STOP_FOREGROUND_REMOVE)
//                    stopSelf()
//                }
//            }
//        })
//
//        return super.onStartCommand(intent, flags, startId)
//    }
//
//    private fun rescheduleNotifications(nonArchivedReminders: List<Reminder>) {
//        nonArchivedReminders
//            .filter { reminder -> reminder.isNotificationSent }
//            .forEach { reminder ->
//                notificationScheduler.scheduleReminderNotification(reminder)
//            }
//    }
//
//    private fun createServiceNotificationChannel() {
//        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//
//        val notificationChannel = NotificationChannel(
//            getString(R.string.notification_channel_id_reminder),
//            getString(R.string.notification_channel_name_reminder),
//            NotificationManager.IMPORTANCE_DEFAULT
//        )
//
//        notificationManager.createNotificationChannel(notificationChannel)
//    }
//
//    private fun getServiceNotification(): Notification {
//        return NotificationCompat.Builder(
//            this,
//            getString(R.string.notification_channel_id_reminder)
//        )
//            .setContentTitle(getString(R.string.app_name))
//            .setContentText(getString(R.string.notification_body_text_reboot_reschedule))
//            .setSmallIcon(R.drawable.ic_user_notification)
//            .build()
//    }

    override fun onBind(intent: Intent?): IBinder? = null
}
