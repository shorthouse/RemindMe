package dev.shorthouse.remindme.util

import dev.shorthouse.remindme.ui.state.ReminderState
import java.time.LocalTime

class ReminderTestUtil {
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
