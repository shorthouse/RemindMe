package dev.shorthouse.remindme.util

import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.model.RepeatInterval
import java.time.LocalTime
import java.time.ZonedDateTime

class ReminderTestUtil {
    fun createReminderState(
        id: Long = 1,
        name: String = "Reminder name",
        date: String = "Sat, 01 Jan 2000",
        time: LocalTime = LocalTime.now(),
        isNotificationSent: Boolean = false,
        isRepeatReminder: Boolean = false,
        repeatAmount: String = "1",
        repeatUnit: String = "Day",
        notes: String? = null,
        isCompleted: Boolean = false
    ): ReminderState {
        return ReminderState(
            id = id,
            name = name,
            date = date,
            time = time,
            isNotificationSent = isNotificationSent,
            isRepeatReminder = isRepeatReminder,
            repeatAmount = repeatAmount,
            repeatUnit = repeatUnit,
            notes = notes,
            isCompleted = isCompleted
        )
    }

    fun createReminderListItemState(
        isNotificationSent: Boolean = false,
        isRepeatReminder: Boolean = false,
    ): ReminderState {
        return ReminderState(
            id = 1,
            name = "Yoga with Alice",
            date = "Wed, 22 Mar 2000",
            time = LocalTime.of(14, 30),
            isNotificationSent = isNotificationSent,
            isRepeatReminder = isRepeatReminder,
            repeatAmount = "2",
            repeatUnit = "Weeks",
            notes = "Don't forget to warm up!",
            isCompleted = false,
        )
    }

    fun createReminder(
        id: Long = 1L,
        name: String = "test reminder",
        startDateTime: ZonedDateTime = ZonedDateTime.now(),
        isNotificationSent: Boolean = false,
        repeatInterval: RepeatInterval? = null,
        notes: String? = null,
        isCompleted: Boolean = false
    ): Reminder {
        return Reminder(
            id = id,
            name = name,
            startDateTime = startDateTime,
            isNotificationSent = isNotificationSent,
            repeatInterval = repeatInterval,
            notes = notes,
            isCompleted = isCompleted
        )
    }
}
