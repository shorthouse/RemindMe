package dev.shorthouse.remindme.domain.reminder

import dev.shorthouse.remindme.data.source.local.ReminderRepository
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.domain.notification.CancelScheduledNotificationUseCase
import dev.shorthouse.remindme.domain.notification.RemoveDisplayingNotificationUseCase
import dev.shorthouse.remindme.domain.notification.ScheduleNotificationUseCase
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.util.floor
import java.time.ZonedDateTime
import javax.inject.Inject
import kotlin.time.DurationUnit
import kotlin.time.times
import kotlin.time.toDuration
import kotlin.time.toKotlinDuration
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class CompleteRepeatReminderOccurrenceUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val scheduleNotificationUseCase: ScheduleNotificationUseCase,
    private val cancelScheduledNotificationUseCase: CancelScheduledNotificationUseCase,
    private val removeDisplayingNotificationUseCase: RemoveDisplayingNotificationUseCase
) {
    private val coroutineScope = CoroutineScope(ioDispatcher)

    operator fun invoke(reminder: Reminder) {
        completeRepeatReminderOccurrence(reminder)
    }

    private fun completeRepeatReminderOccurrence(reminder: Reminder) {
        coroutineScope.launch {
            val updatedReminder = reminder.copy(
                startDateTime = getUpdatedReminderStartDateTime(reminder)
            )

            reminderRepository.updateReminder(updatedReminder)

            cancelScheduledNotificationUseCase(reminder)
            removeDisplayingNotificationUseCase(reminder)

            if (reminder.isNotificationSent) {
                scheduleNotificationUseCase(reminder)
            }
        }
    }

    private fun getUpdatedReminderStartDateTime(reminder: Reminder): ZonedDateTime {
        val repeatInterval = reminder.repeatInterval ?: return reminder.startDateTime

        val repeatDuration = repeatInterval.unit.duration
            .multipliedBy(repeatInterval.amount)
            .toKotlinDuration()

        if (reminder.startDateTime.isAfter(ZonedDateTime.now())) {
            return reminder.startDateTime.plusSeconds(repeatDuration.inWholeSeconds)
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
}