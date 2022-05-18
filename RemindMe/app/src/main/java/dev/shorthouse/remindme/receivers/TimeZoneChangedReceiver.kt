package dev.shorthouse.remindme.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dev.shorthouse.remindme.services.UpdateReminderTimeZoneService
import dev.shorthouse.remindme.utilities.UPDATED_TIME_ZONE_EXTRA

class TimeZoneChangedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("HDS", "TIMEZONE HAS CHANGED BROADCAST RECEIVER")
        if (intent?.action == "android.intent.action.TIMEZONE_CHANGED" && context != null) {
            Log.d("HDS", "timezone is: ${intent.getStringExtra(UPDATED_TIME_ZONE_EXTRA)}")
            val serviceIntent = Intent(context, UpdateReminderTimeZoneService::class.java)

            serviceIntent.putExtra(
                UPDATED_TIME_ZONE_EXTRA,
                intent.getStringExtra(UPDATED_TIME_ZONE_EXTRA)
            )
            context.startForegroundService(serviceIntent)
        }
    }
}