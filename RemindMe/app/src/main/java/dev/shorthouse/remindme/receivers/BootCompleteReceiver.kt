package dev.shorthouse.remindme.receivers

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import dev.shorthouse.remindme.data.ReminderDatabase
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.services.RescheduleAlarmsOnBootService
import dev.shorthouse.remindme.utilities.AlarmHelper
import dev.shorthouse.remindme.utilities.DAYS_IN_WEEK
import java.time.Duration
import java.time.temporal.ChronoUnit

class BootCompleteReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.intent.action.BOOT_COMPLETED" && context != null) {
            val serviceIntent = Intent(context, RescheduleAlarmsOnBootService::class.java)
            context.startService(serviceIntent)
            Log.d("HDS", "start service called in broadcast receiver")
        }
    }

    fun onReceiveOld(context: Context?, intent: Intent?) {
        if (intent?.action == "android.intent.action.BOOT_COMPLETED" && context != null) {
            val alarmHelper = AlarmHelper()

            val remindersLiveData =
                ReminderDatabase.getDatabase(context)
                    .reminderDao()
                    .getAllNonArchivedReminders()
                    .asLiveData()

            remindersLiveData.observeForever(object : Observer<List<Reminder>> {
                override fun onChanged(reminders: List<Reminder>?) {


                    remindersLiveData.removeObserver(this)
                }
            })

            remindersLiveData.observeForever { reminders ->
                val notifyingReminders = reminders.filter { it.isNotificationSent }

                notifyingReminders.forEach { reminder ->
                    alarmHelper.setNotificationAlarm(
                        context,
                        reminder,
                        getTriggerAtMillis(reminder),
                        getRepeatIntervalMillis(reminder)
                    )
                }

                remindersLiveData.removeObserver(this)

            }

        }
    }

    private fun getTriggerAtMillis(reminder: Reminder): Long {
        return reminder.startDateTime.toInstant().toEpochMilli()
    }

    private fun getRepeatIntervalMillis(reminder: Reminder): Long? {
        val repeatInterval = reminder.repeatInterval ?: return null

        val repeatIntervalDays = when (repeatInterval.timeUnit) {
            ChronoUnit.DAYS -> repeatInterval.timeValue
            else -> repeatInterval.timeValue * DAYS_IN_WEEK
        }

        return Duration.ofDays(repeatIntervalDays).toMillis()
    }


}