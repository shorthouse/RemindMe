package dev.shorthouse.remindme.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import dagger.hilt.android.AndroidEntryPoint
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.data.ReminderRepository
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
import javax.inject.Inject

@AndroidEntryPoint
class UpdateDoneReminderService : Service() {

    @Inject
    lateinit var repository: ReminderRepository

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val reminderId = intent?.getLongExtra(getString(R.string.intent_key_reminderId), -1L)
        if (reminderId == null || reminderId == -1L) return START_NOT_STICKY

        val reminderLiveData = repository.getReminder(reminderId).asLiveData()

        reminderLiveData.observeForever(object : Observer<Reminder> {
            override fun onChanged(reminder: Reminder?) {
                reminder?.let {
                    reminderLiveData.removeObserver(this)
                    insertUpdatedDoneReminder(
                        getUpdatedDoneReminder(
                            reminder.id,
                            reminder.name,
                            reminder.startDateTime,
                            reminder.repeatInterval,
                            reminder.notes,
                            reminder.isNotificationSent
                        )
                    )
                }
            }
        })

        return super.onStartCommand(intent, flags, startId)
    }

    private fun insertUpdatedDoneReminder(updatedDoneReminder: Reminder) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.insertReminder(updatedDoneReminder)

            NotificationManagerCompat.from(this@UpdateDoneReminderService)
                .cancel(updatedDoneReminder.id.toInt())

            this@UpdateDoneReminderService.stopSelf()
        }
    }

    fun getUpdatedDoneReminder(
        id: Long,
        name: String,
        startDateTime: ZonedDateTime,
        repeatInterval: RepeatInterval?,
        notes: String?,
        isNotificationSent: Boolean,
    ): Reminder {
        return when (repeatInterval) {
            null -> getCompletedSingleReminder(
                id,
                name,
                startDateTime,
                notes,
                isNotificationSent,
            )
            else -> getUpdatedRepeatReminder(
                id,
                name,
                startDateTime,
                repeatInterval,
                notes,
                isNotificationSent,
            )
        }
    }

    private fun getCompletedSingleReminder(
        id: Long,
        name: String,
        startDateTime: ZonedDateTime,
        notes: String?,
        isNotificationSent: Boolean,
    ): Reminder {
        return Reminder(
            id = id,
            name = name,
            startDateTime = startDateTime,
            repeatInterval = null,
            notes = notes,
            isArchived = true,
            isNotificationSent = isNotificationSent,
        )
    }

    private fun getUpdatedRepeatReminder(
        id: Long,
        name: String,
        startDateTime: ZonedDateTime,
        repeatInterval: RepeatInterval,
        notes: String?,
        isNotificationSent: Boolean,
    ): Reminder {
        return Reminder(
            id = id,
            name = name,
            startDateTime = getUpdatedStartDateTime(startDateTime, repeatInterval),
            repeatInterval = repeatInterval,
            notes = notes,
            isArchived = false,
            isNotificationSent = isNotificationSent,
        )
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
