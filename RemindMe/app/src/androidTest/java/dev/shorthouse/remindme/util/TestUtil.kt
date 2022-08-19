package dev.shorthouse.remindme.util

import dev.shorthouse.remindme.data.RepeatInterval
import dev.shorthouse.remindme.model.Reminder
import java.time.ZonedDateTime

object TestUtil {
    fun createReminder(
        id: Long = 1L,
        name: String = "test reminder",
        startDateTime: ZonedDateTime = ZonedDateTime.now(),
        repeatInterval: RepeatInterval? = null,
        notes: String? = null,
        isArchived: Boolean = false,
        isNotificationSent: Boolean = false
    ) = Reminder (
            id = id,
            name = name,
            startDateTime = startDateTime,
            repeatInterval = repeatInterval,
            notes = notes,
            isArchived = isArchived,
            isNotificationSent = isNotificationSent
        )
}