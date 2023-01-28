package dev.shorthouse.remindme.util

import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.model.RepeatInterval
import java.time.ZonedDateTime

object TestUtil {
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
}
