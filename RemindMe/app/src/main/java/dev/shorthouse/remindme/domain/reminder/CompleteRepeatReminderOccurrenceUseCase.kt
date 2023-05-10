package dev.shorthouse.remindme.domain.reminder

import dev.shorthouse.remindme.data.source.local.ReminderRepository
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.domain.notification.CancelScheduledNotificationUseCase
import dev.shorthouse.remindme.domain.notification.ScheduleNotificationUseCase
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.util.floor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import javax.inject.Inject
import kotlin.time.DurationUnit
import kotlin.time.times
import kotlin.time.toDuration
import kotlin.time.toKotlinDuration

class CompleteRepeatReminderOccurrenceUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val scheduleNotificationUseCase: ScheduleNotificationUseCase,
    private val cancelScheduledNotificationUseCase: CancelScheduledNotificationUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(reminder: Reminder) {
        completeRepeatReminderOccurrence(reminder)
    }

    private suspend fun completeRepeatReminderOccurrence(reminder: Reminder) {
        withContext(ioDispatcher) {
            val updatedReminder = reminder.copy(
                startDateTime = getUpdatedReminderStartDateTime(reminder)
            )

            reminderRepository.updateReminder(updatedReminder)

            cancelScheduledNotificationUseCase(reminder)

            if (reminder.isNotificationSent) {
                scheduleNotificationUseCase(reminder)
            }
        }
    }

    private fun getUpdatedReminderStartDateTime(reminder: Reminder): ZonedDateTime {
        val repeatInterval = reminder.repeatInterval ?: return reminder.startDateTime

        val repeatDuration = repeatInterval.unit.duration
            .multipliedBy(repeatInterval.amount.toLong())
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
