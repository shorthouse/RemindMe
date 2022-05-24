package dev.shorthouse.remindme.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.shorthouse.remindme.services.UpdateReminderTimeZoneService
import dev.shorthouse.remindme.utilities.UPDATED_TIME_ZONE_EXTRA

class TimeZoneChangedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.intent.action.TIMEZONE_CHANGED" && context != null) {
            val serviceIntent = Intent(context, UpdateReminderTimeZoneService::class.java)

            serviceIntent.putExtra(
                UPDATED_TIME_ZONE_EXTRA,
                intent.getStringExtra(UPDATED_TIME_ZONE_EXTRA)
            )
            context.startForegroundService(serviceIntent)
        }
    }
}