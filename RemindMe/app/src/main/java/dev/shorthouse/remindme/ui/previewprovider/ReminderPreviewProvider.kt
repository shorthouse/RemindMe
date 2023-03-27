package dev.shorthouse.remindme.ui.previewprovider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.model.RepeatInterval
import dev.shorthouse.remindme.ui.previewprovider.ReminderPreviewData.addNotes
import dev.shorthouse.remindme.ui.previewprovider.ReminderPreviewData.addNotification
import dev.shorthouse.remindme.ui.previewprovider.ReminderPreviewData.addRepeatInterval
import dev.shorthouse.remindme.ui.previewprovider.ReminderPreviewData.completed
import dev.shorthouse.remindme.ui.previewprovider.ReminderPreviewData.default
import dev.shorthouse.remindme.ui.previewprovider.ReminderPreviewData.overdue
import dev.shorthouse.remindme.ui.previewprovider.ReminderPreviewData.upcoming
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class DefaultReminderProvider : PreviewParameterProvider<Reminder> {
    override val values = sequenceOf(
        default
    )
}

class ReminderListProvider : PreviewParameterProvider<List<Reminder>> {
    override val values = sequenceOf(
        listOf(
            upcoming,
            overdue,
            completed,
            addNotification,
            addRepeatInterval,
            addNotes
        ),
        emptyList()
    )
}

class ReminderCardProvider : PreviewParameterProvider<Reminder> {
    override val values = sequenceOf(
        upcoming,
        overdue,
        completed,
        addNotification,
        addRepeatInterval,
        addNotes
    )
}

private object ReminderPreviewData {
    val default = Reminder(
        id = 1,
        name = "Water the plants",
        startDateTime = ZonedDateTime.parse("2020-03-22T14:30:00Z"),
        isNotificationSent = true,
        repeatInterval = RepeatInterval(2, ChronoUnit.WEEKS),
        notes = "The orchids need less water",
        isCompleted = false
    )

    val overdue = Reminder(
        id = 2,
        name = "Overdue",
        startDateTime = ZonedDateTime.parse("2020-01-01T08:30:00Z"),
        isNotificationSent = false,
        repeatInterval = null,
        notes = null,
        isCompleted = false
    )

    val upcoming = Reminder(
        id = 3,
        name = "Upcoming",
        startDateTime = ZonedDateTime.parse("3020-01-01T08:30:00Z"),
        isNotificationSent = false,
        repeatInterval = null,
        notes = null,
        isCompleted = false
    )

    val completed = Reminder(
        id = 4,
        name = "Completed",
        startDateTime = ZonedDateTime.parse("2020-01-01T08:30:00Z"),
        isNotificationSent = false,
        repeatInterval = null,
        notes = null,
        isCompleted = true
    )

    val addNotification = Reminder(
        id = 5,
        name = "Add notification",
        startDateTime = ZonedDateTime.parse("3020-01-01T08:30:00Z"),
        isNotificationSent = true,
        repeatInterval = null,
        notes = null,
        isCompleted = false
    )

    val addRepeatInterval = Reminder(
        id = 6,
        name = "Add repeat interval",
        startDateTime = ZonedDateTime.parse("3020-01-01T08:30:00Z"),
        isNotificationSent = true,
        repeatInterval = RepeatInterval(2, ChronoUnit.WEEKS),
        notes = null,
        isCompleted = false
    )

    val addNotes = Reminder(
        id = 7,
        name = "Add notes",
        startDateTime = ZonedDateTime.parse("3020-01-01T08:30:00Z"),
        isNotificationSent = true,
        repeatInterval = RepeatInterval(2, ChronoUnit.WEEKS),
        notes = "Don't forget to do this thing",
        isCompleted = false
    )
}
