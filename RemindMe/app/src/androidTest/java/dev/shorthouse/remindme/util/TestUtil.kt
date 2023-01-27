package dev.shorthouse.remindme.util

import dev.shorthouse.remindme.compose.preview.PreviewData
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.model.RepeatInterval
import java.time.LocalTime
import java.time.ZonedDateTime

class TestUtil {
    companion object {
        val reminderAddState = ReminderState(
            id = 1,
            name = "",
            date = "Sat, 01 Jan 2000",
            time = LocalTime.of(14, 30),
            isNotificationSent = false,
            isRepeatReminder = false,
            repeatAmount = "1",
            repeatUnit = "Day",
            notes = "",
            isCompleted = false
        )

        val reminderEditState = ReminderState(
            id = 1,
            name = "",
            date = "Sat, 01 Jan 2000",
            time = LocalTime.of(14, 30),
            isNotificationSent = false,
            isRepeatReminder = false,
            repeatAmount = "1",
            repeatUnit = "Day",
            notes = "",
            isCompleted = false
        )

        val reminderListStates = listOf(
            PreviewData.reminderState,
            ReminderState(
                id = 2,
                name = "Feed the fish",
                date = "Fri, 27 Jun 2017",
                time = LocalTime.of(18, 15),
                isNotificationSent = true,
                isRepeatReminder = false,
                repeatAmount = "",
                repeatUnit = "",
                notes = null,
                isCompleted = false
            ),
            ReminderState(
                id = 3,
                name = "Go for a run",
                date = "Wed, 07 Jan 2022",
                time = LocalTime.of(7, 15),
                isNotificationSent = false,
                isRepeatReminder = false,
                repeatAmount = "",
                repeatUnit = "",
                notes = "The cardio will be worth it!",
                isCompleted = false
            )
        )
        val reminderStateList = listOf(
            PreviewData.reminderState,
            ReminderState(
                id = 2,
                name = "Feed the fish",
                date = "Fri, 27 Jun 2017",
                time = LocalTime.of(18, 15),
                isNotificationSent = true,
                isRepeatReminder = false,
                repeatAmount = "",
                repeatUnit = "",
                notes = null,
                isCompleted = false
            ),
            ReminderState(
                id = 3,
                name = "Go for a run",
                date = "Wed, 07 Jan 2022",
                time = LocalTime.of(7, 15),
                isNotificationSent = false,
                isRepeatReminder = false,
                repeatAmount = "",
                repeatUnit = "",
                notes = "The cardio will be worth it!",
                isCompleted = false
            )
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

    fun createReminderAddState(): ReminderState {
        return ReminderState(
            id = 1,
            name = "",
            date = "Sat, 01 Jan 2000",
            time = LocalTime.of(14, 30),
            isNotificationSent = false,
            isRepeatReminder = false,
            repeatAmount = "1",
            repeatUnit = "Day",
            notes = "",
            isCompleted = false
        )
    }

    fun createReminderEditState(): ReminderState {
        return ReminderState(
            id = 1,
            name = "Reminder name",
            date = "Sun, 02 Jan 2000",
            time = LocalTime.of(18, 30),
            isNotificationSent = true,
            isRepeatReminder = true,
            repeatAmount = "2",
            repeatUnit = "Weeks",
            notes = "Reminder notes",
            isCompleted = false
        )
    }
}
