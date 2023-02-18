package dev.shorthouse.remindme.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.shorthouse.remindme.service.RescheduleNotificationsOnRebootService

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.intent.action.BOOT_COMPLETED" && context != null) {
            val serviceIntent = Intent(context, RescheduleNotificationsOnRebootService::class.java)
            context.startForegroundService(serviceIntent)
        }
    }
}
