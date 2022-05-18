package dev.shorthouse.remindme.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.shorthouse.remindme.services.RescheduleNotificationsService

class BootCompleteReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.intent.action.BOOT_COMPLETED" && context != null) {
            val serviceIntent = Intent(context, RescheduleNotificationsService::class.java)
            context.startForegroundService(serviceIntent)
        }
    }
}