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
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.util.DAYS_IN_WEEK
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@AndroidEntryPoint
class UpdateDoneReminderService : Service() {

    @Inject
    @IoDispatcher
    lateinit var ioDispatcher: CoroutineDispatcher

    @Inject
    lateinit var repository: ReminderRepository

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val reminderId = intent?.getLongExtra(getString(R.string.intent_key_reminderId), -1L)
        if (reminderId == null || reminderId == -1L) return START_NOT_STICKY

        observeReminder(reminderId)

        return super.onStartCommand(intent, flags, startId)
    }

    private fun observeReminder(reminderId: Long) {
        val reminderLiveData = repository.getReminder(reminderId).asLiveData()

        reminderLiveData.observeForever(object : Observer<Reminder> {
            override fun onChanged(reminder: Reminder?) {
                reminder?.let {
                    reminderLiveData.removeObserver(this)
                    updateDoneReminder(reminder)
                }
            }
        })
    }

    private fun updateDoneReminder(reminder: Reminder) {
        val updatedDoneReminder = getUpdatedDoneReminder(reminder)

        CoroutineScope(ioDispatcher).launch {
            repository.updateReminder(updatedDoneReminder)
        }

        cancelReminderNotification(updatedDoneReminder.id)
        stopSelf()
    }

    private fun cancelReminderNotification(reminderId: Long) {
        NotificationManagerCompat.from(this@UpdateDoneReminderService)
            .cancel(reminderId.toInt())
    }

    private fun getUpdatedDoneReminder(reminder: Reminder): Reminder {
        return Reminder(
            id = reminder.id,
            name = reminder.name,
            startDateTime = getUpdatedStartDateTime(reminder),
            repeatInterval = reminder.repeatInterval,
            notes = reminder.notes,
            isCompleted = !reminder.isRepeatReminder(),
            isNotificationSent = reminder.isNotificationSent,
        )
    }

    private fun getUpdatedStartDateTime(reminder: Reminder): ZonedDateTime {
        val repeatInterval = reminder.repeatInterval ?: return reminder.startDateTime

        val repeatDuration = when (repeatInterval.unit) {
            ChronoUnit.DAYS -> Duration.ofDays(repeatInterval.amount)
            else -> Duration.ofDays(repeatInterval.amount * DAYS_IN_WEEK)
        }

        val passedDuration = Duration.between(reminder.startDateTime, ZonedDateTime.now())

        val secondsToNextReminder = passedDuration.dividedBy(repeatDuration)
            .plus(1)
            .times(repeatDuration.toSeconds())

        return reminder.startDateTime.plusSeconds(secondsToNextReminder)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
