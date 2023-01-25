package dev.shorthouse.remindme.compose.state

import java.time.LocalTime

object PreviewData {
    val reminderStateEmpty = ReminderState()

    val reminderState = ReminderState(
        id = 1,
        name = "Yoga with Alice",
        date = "Wed, 22 Mar 2000",
        time = LocalTime.of(14, 30),
        isNotificationSent = true,
        isRepeatReminder = true,
        repeatAmount = "2",
        repeatUnit = "Weeks",
        notes = "Don't forget to warm up!"
    )

    val reminderStateList = listOf(
        reminderState,
        ReminderState(
            id = 2,
            name = "Feed the fish",
            date = "Fri, 27 Jun 2017",
            time = LocalTime.of(18, 15),
            isNotificationSent = true,
            isRepeatReminder = false,
            repeatAmount = "",
            repeatUnit = "",
            notes = null
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
            notes = "The cardio will be worth it!"
        )
    )
}
