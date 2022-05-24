package dev.shorthouse.remindme.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.services.UpdateReminderTimeZoneService
import dev.shorthouse.remindme.utilities.UPDATED_TIME_ZONE_EXTRA

class TimeZoneChangedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.intent.action.TIMEZONE_CHANGED" && context != null) {
            val serviceIntent = Intent(context, UpdateReminderTimeZoneService::class.java)
            val newTimeZone = intent.getStringExtra(UPDATED_TIME_ZONE_EXTRA)

            serviceIntent.putExtra(context.getString(R.string.intent_key_timeZone), newTimeZone)
            context.startForegroundService(serviceIntent)
        }
    }
}