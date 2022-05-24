package dev.shorthouse.remindme.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.shorthouse.remindme.services.UpdateDoneReminderService

class NotificationActionDoneReceiver : BroadcastReceiver() {
    companion object {
        const val REMINDER_ID = "reminderId"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            val reminderId = intent.getLongExtra(REMINDER_ID, -1L)
            if (reminderId != -1L) {
                val serviceIntent = Intent(context, UpdateDoneReminderService::class.java)
                    .putExtra(REMINDER_ID, reminderId)
                context.startService(serviceIntent)
            }
        }
    }
}