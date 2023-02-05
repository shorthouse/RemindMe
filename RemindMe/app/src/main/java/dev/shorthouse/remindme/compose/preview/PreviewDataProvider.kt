package dev.shorthouse.remindme.compose.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import dev.shorthouse.remindme.compose.state.ReminderState
import java.time.LocalTime

class PreviewData {
    companion object {
        val previewEmptyReminderState = ReminderState()

        val previewReminderState = ReminderState(
            id = 1,
            name = "Yoga with Alice",
            date = "Wed, 22 Mar 2000",
            time = LocalTime.of(14, 30),
            isNotificationSent = true,
            isRepeatReminder = true,
            repeatAmount = "2",
            repeatUnit = "Weeks",
            notes = "Don't forget to warm up!",
            isCompleted = false
        )

        val previewReminderStates = listOf(
            previewReminderState,
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
}

class ReminderListCardProvider : PreviewParameterProvider<ReminderState> {
    override val values = sequenceOf(
        ReminderState(
            id = 1,
            name = "Scheduled",
            date = "Sat, 01 Jan 3020",
            time = LocalTime.of(8, 30),
            isNotificationSent = false,
            isRepeatReminder = false,
            repeatAmount = "",
            repeatUnit = "",
            notes = null,
            isCompleted = false
        ),
        ReminderState(
            id = 2,
            name = "Overdue",
            date = "Wed, 01 Jan 2020",
            time = LocalTime.of(8, 30),
            isNotificationSent = false,
            isRepeatReminder = false,
            repeatAmount = "",
            repeatUnit = "",
            notes = null,
            isCompleted = false
        ),
        ReminderState(
            id = 3,
            name = "Completed",
            date = "Wed, 01 Jan 2020",
            time = LocalTime.of(8, 30),
            isNotificationSent = false,
            isRepeatReminder = false,
            repeatAmount = "",
            repeatUnit = "",
            notes = null,
            isCompleted = true
        ),
        ReminderState(
            id = 4,
            name = "Add notification",
            date = "Sat, 01 Jan 3020",
            time = LocalTime.of(8, 30),
            isNotificationSent = true,
            isRepeatReminder = false,
            repeatAmount = "",
            repeatUnit = "",
            notes = null,
            isCompleted = false
        ),
        ReminderState(
            id = 5,
            name = "Add repeat interval",
            date = "Sat, 01 Jan 3020",
            time = LocalTime.of(8, 30),
            isNotificationSent = true,
            isRepeatReminder = true,
            repeatAmount = "4",
            repeatUnit = "days",
            notes = null,
            isCompleted = false
        ),
        ReminderState(
            id = 6,
            name = "Add notes",
            date = "Sat, 01 Jan 3020",
            time = LocalTime.of(8, 30),
            isNotificationSent = true,
            isRepeatReminder = true,
            repeatAmount = "4",
            repeatUnit = "days",
            notes = "Don't forget to do this thing",
            isCompleted = false
        ),
    )
}