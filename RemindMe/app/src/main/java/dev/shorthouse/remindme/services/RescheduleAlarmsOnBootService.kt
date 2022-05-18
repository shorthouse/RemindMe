package dev.shorthouse.remindme.services

import android.app.*
import android.app.job.JobService
import android.content.Intent
import android.os.*
import android.util.Log
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import dev.shorthouse.remindme.R


class RescheduleAlarmsOnBootService : JobService() {
    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null
    private val CHANNEL_ID = "NOTIFICATION_CHANNEL"


    override fun onBind(intent: Intent?): IBinder? {
        return null // Binding not allowed
    }

    override fun onCreate() {
        createNotificationChannel()

        HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND).apply {
            start()
            // Get the HandlerThread's Looper and use it for our Handler
            serviceLooper = looper
            serviceHandler = ServiceHandler(looper)
        }

        startForeground(1, getNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("HDS", "Service onStartCommand")


        return START_NOT_STICKY

    }

    override fun onDestroy() {
        Log.d("HDS", "serivce onDestroy")
    }

    private fun getNotification(): Notification {
        //  .setContentTitle(getText(R.string.notification_title))
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("RemindMe")
            .setContentText("Scheduling reminder notifications...")
            .setSmallIcon(R.drawable.ic_notification)
            .build()

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val appName = getString(R.string.app_name)
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                appName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }
}