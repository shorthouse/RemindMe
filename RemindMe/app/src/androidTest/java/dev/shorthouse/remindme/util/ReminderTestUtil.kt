package dev.shorthouse.remindme.util

import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.model.RepeatInterval
import dev.shorthouse.remindme.ui.state.ReminderState
import java.time.LocalTime
import java.time.ZonedDateTime

class ReminderTestUtil {
    fun createReminder(
        id: Long = 1L,
        name: String = "Test Reminder",
        startDateTime: ZonedDateTime = ZonedDateTime.now(),
        repeatInterval: RepeatInterval? = null,
        notes: String? = null,
        isCompleted: Boolean = false,
        isNotificationSent: Boolean = false
    ): Reminder {
        return Reminder(
            id = id,
            name = name,
            startDateTime = startDateTime,
            repeatInterval = repeatInterval,
            notes = notes,
            isCompleted = isCompleted,
            isNotificationSent = isNotificationSent
        )
    }

    fun createReminderState(
        id: Long = 1,
        name: String = "Reminder name",
        date: String = "Sat, 01 Jan 2000",
        time: LocalTime = LocalTime.parse("08:30"),
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
}
