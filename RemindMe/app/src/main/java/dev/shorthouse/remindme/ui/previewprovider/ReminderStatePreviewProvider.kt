package dev.shorthouse.remindme.ui.previewprovider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import dev.shorthouse.remindme.ui.previewprovider.ReminderStatePreviewData.default
import dev.shorthouse.remindme.ui.previewprovider.ReminderStatePreviewData.empty
import dev.shorthouse.remindme.ui.state.ReminderState
import java.time.LocalTime

class DefaultReminderStateProvider : PreviewParameterProvider<ReminderState> {
    override val values = sequenceOf(
        default
    )
}

class EmptyReminderStateProvider : PreviewParameterProvider<ReminderState> {
    override val values = sequenceOf(
        empty
    )
}

private object ReminderStatePreviewData {
    val empty = ReminderState()

    val default = ReminderState(
        id = 1,
        name = "Water the plants",
        date = "Sun, 22 Mar 2020",
        time = LocalTime.of(14, 30),
        isNotificationSent = true,
        isRepeatReminder = true,
        repeatAmount = "2",
        repeatUnit = "Weeks",
        notes = "The orchids need less water",
        isCompleted = false
    )
}
