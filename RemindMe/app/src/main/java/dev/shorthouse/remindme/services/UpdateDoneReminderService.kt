package dev.shorthouse.remindme.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import dev.shorthouse.remindme.data.ReminderDatabase
import dev.shorthouse.remindme.data.RepeatInterval
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.utilities.DAYS_IN_WEEK
import dev.shorthouse.remindme.utilities.ONE_INTERVAL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class UpdateDoneReminderService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) return START_NOT_STICKY
        val reminderId = intent.getLongExtra("reminderId", -1L)
        if (reminderId == -1L) return START_NOT_STICKY

        val reminderLiveData =
            ReminderDatabase.getDatabase(this)
                .reminderDao()
                .getReminder(reminderId)
                .asLiveData()

        reminderLiveData.observeForever(object : Observer<Reminder> {
            override fun onChanged(reminder: Reminder?) {
                if (reminder == null) return
                reminderLiveData.removeObserver(this)
                updateDoneReminder(
                    reminder.id,
                    reminder.name,
                    reminder.startDateTime,
                    reminder.repeatInterval,
                    reminder.notes,
                    reminder.isNotificationSent
                )
            }
        })

        return super.onStartCommand(intent, flags, startId)

    }

    fun updateDoneReminder(
        id: Long,
        name: String,
        startDateTime: ZonedDateTime,
        repeatInterval: RepeatInterval?,
        notes: String?,
        isNotificationSent: Boolean,
    ) {
        when (repeatInterval) {
            null -> updateDoneSingleReminder(
                id,
                name,
                startDateTime,
                notes,
                isNotificationSent,
            )
            else -> updateDoneRepeatReminder(
                id,
                name,
                startDateTime,
                repeatInterval,
                notes,
                isNotificationSent,
            )
        }
    }

    private fun updateDoneSingleReminder(
        id: Long,
        name: String,
        startDateTime: ZonedDateTime,
        notes: String?,
        isNotificationSent: Boolean,
    ) {
        val reminder = Reminder(
            id = id,
            name = name,
            startDateTime = startDateTime,
            repeatInterval = null,
            notes = notes,
            isArchived = true,
            isNotificationSent = isNotificationSent,
        )

        CoroutineScope(Dispatchers.IO).launch {
            ReminderDatabase.getDatabase(this@UpdateDoneReminderService)
                .reminderDao()
                .update(reminder)
            NotificationManagerCompat.from(this@UpdateDoneReminderService)
                .cancel(id.toInt())
            this@UpdateDoneReminderService.stopSelf()
        }
    }

    private fun updateDoneRepeatReminder(
        id: Long,
        name: String,
        startDateTime: ZonedDateTime,
        repeatInterval: RepeatInterval,
        notes: String?,
        isNotificationSent: Boolean,
    ) {
        val reminder = Reminder(
            id = id,
            name = name,
            startDateTime = getUpdatedStartDateTime(startDateTime, repeatInterval),
            repeatInterval = repeatInterval,
            notes = notes,
            isArchived = false,
            isNotificationSent = isNotificationSent,
        )

        CoroutineScope(Dispatchers.IO).launch {
            ReminderDatabase.getDatabase(this@UpdateDoneReminderService)
                .reminderDao()
                .update(reminder)
            NotificationManagerCompat.from(this@UpdateDoneReminderService)
                .cancel(id.toInt())
            this@UpdateDoneReminderService.stopSelf()
        }
    }

    private fun getUpdatedStartDateTime(
        startDateTime: ZonedDateTime,
        repeatInterval: RepeatInterval,
    ): ZonedDateTime {
        val period = Period.between(startDateTime.toLocalDate(), LocalDate.now())
        val timeValue = repeatInterval.timeValue

        return when (repeatInterval.timeUnit) {
            ChronoUnit.DAYS -> {
                val passedDays = period.days
                val passedIntervals = passedDays.div(timeValue)
                val nextInterval = passedIntervals.plus(ONE_INTERVAL)
                val daysUntilNextStart = timeValue * nextInterval
                startDateTime.plusDays(daysUntilNextStart)
            }
            else -> {
                val passedWeeks = period.days.div(DAYS_IN_WEEK)
                val passedIntervals = passedWeeks.div(timeValue)
                val nextInterval = passedIntervals.plus(ONE_INTERVAL)
                val weeksUntilNextStart = timeValue * nextInterval
                startDateTime.plusWeeks(weeksUntilNextStart)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}