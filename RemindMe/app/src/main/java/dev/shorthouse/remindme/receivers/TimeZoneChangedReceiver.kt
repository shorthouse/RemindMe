package dev.shorthouse.remindme.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.services.UpdateReminderTimeZoneService

class TimeZoneChangedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.intent.action.TIMEZONE_CHANGED" && context != null) {
            val newTimeZone = intent.getStringExtra(context.getString(R.string.intent_key_timeZone))
            val serviceIntent = Intent(context, UpdateReminderTimeZoneService::class.java)
            serviceIntent.putExtra(context.getString(R.string.intent_key_timeZone), newTimeZone)

            context.startForegroundService(serviceIntent)
        }
    }
}
