package dev.shorthouse.remindme.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.service.NotificationCompleteReminderService

class NotificationActionDoneReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val reminderId = intent?.getLongExtra(
            context?.getString(R.string.intent_key_reminderId),
            -1L
        )

        if (context != null && reminderId != null) {
            val serviceIntent = Intent(
                context,
                NotificationCompleteReminderService::class.java
            )
                .putExtra(
                    context.getString(R.string.intent_key_reminderId),
                    reminderId
                )

            context.startService(serviceIntent)
        }
    }
}
