package dev.shorthouse.remindme.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.utilities.DAYS_IN_WEEK
import dev.shorthouse.remindme.utilities.NotificationScheduler
import dev.shorthouse.remindme.utilities.floor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.time.Duration.Companion.days
import kotlin.time.DurationUnit
import kotlin.time.times
import kotlin.time.toDuration

@HiltViewModel
class ListActiveViewModel @Inject constructor(
    private val repository: ReminderRepository,
    private val notificationScheduler: NotificationScheduler,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    val activeReminders = repository.getActiveReminders().asLiveData()
    private val currentTime = MutableLiveData(ZonedDateTime.now())

    init {
        viewModelScope.launch {
            delay(getMillisUntilNextMinute())

            while (true) {
                launch {
                    currentTime.value = ZonedDateTime.now()
                }
                delay(Duration.ofMinutes(1).toMillis())
            }
        }
    }

    fun updateDoneReminder(reminder: Reminder) {
        viewModelScope.launch(ioDispatcher) {
            val updatedReminder = Reminder(
                id = reminder.id,
                name = reminder.name,
                startDateTime = getUpdatedReminderStartDateTime(reminder),
                repeatInterval = reminder.repeatInterval,
                notes = reminder.notes,
                isComplete = !reminder.isRepeatReminder(),
                isNotificationSent = reminder.isNotificationSent,
            )

            repository.updateReminder(updatedReminder)
        }
    }

    fun removeDisplayingNotification(reminder: Reminder) {
        notificationScheduler.removeDisplayingNotification(reminder.id.toInt())
    }

    private fun getUpdatedReminderStartDateTime(reminder: Reminder): ZonedDateTime {
        val repeatInterval = reminder.repeatInterval ?: return reminder.startDateTime

        val repeatDuration = when (repeatInterval.unit) {
            ChronoUnit.DAYS -> repeatInterval.amount.days
            else -> (repeatInterval.amount * DAYS_IN_WEEK).days
        }

        val secondsToNewStartDateTime = ZonedDateTime.now()
            .toEpochSecond()
            .minus(reminder.startDateTime.toEpochSecond())
            .toDuration(DurationUnit.SECONDS)
            .div(repeatDuration)
            .floor()
            .inc()
            .times(repeatDuration)
            .inWholeSeconds

        return reminder.startDateTime.plusSeconds(secondsToNewStartDateTime)
    }

    private fun getMillisUntilNextMinute(): Long {
        val now = LocalDateTime.now()
        val nextMinute = now.plusMinutes(1).truncatedTo(ChronoUnit.MINUTES)
        return Duration.between(now, nextMinute).toMillis()
    }
}
