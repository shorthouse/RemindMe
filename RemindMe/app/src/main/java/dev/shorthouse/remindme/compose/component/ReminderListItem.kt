package dev.shorthouse.remindme.compose.component

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.theme.RemindMeTheme
import dev.shorthouse.remindme.theme.SubtitleGrey
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalTime
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun OverdueReminderListItem(
    reminderState: ReminderState,
    onCompleteChecked: (Reminder) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        ReminderListItem(reminderState = reminderState, modifier = Modifier.weight(1f))

        var selected by remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()

        ReminderListItemCheckbox(
            selected = selected,
            onChecked = {
                coroutineScope.launch {
                    selected = true
                    delay(200.milliseconds)
                    onCompleteChecked(reminderState.toReminder())
                    selected = false
                }
            }
        )
    }
}

@Composable
fun ScheduledReminderListItem(
    reminderState: ReminderState,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth()) {
        ReminderListItem(reminderState = reminderState)
    }
}

@Composable
fun CompletedReminderListItem(
    reminderState: ReminderState,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth()) {
        ReminderListItem(reminderState = reminderState)
    }
}

@Composable
fun ReminderListItem(reminderState: ReminderState, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = reminderState.name,
            style = MaterialTheme.typography.body2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.margin_tiny)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = reminderState.date,
                style = MaterialTheme.typography.subtitle2
            )

            Text(
                text = reminderState.time.toString(),
                style = MaterialTheme.typography.subtitle2
            )

            if (reminderState.isNotificationSent) {
                Icon(
                    imageVector = Icons.Rounded.NotificationsNone,
                    contentDescription = stringResource(R.string.cd_notification_sent),
                    tint = SubtitleGrey,
                    modifier = Modifier.size(dimensionResource(R.dimen.margin_normal))
                )
            }

            if (reminderState.isRepeatReminder) {
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = stringResource(R.string.cd_repeat_reminder),
                    tint = SubtitleGrey,
                    modifier = Modifier.size(dimensionResource(R.dimen.margin_normal))
                )
            }
        }

    }
}

@Composable
fun ReminderListItemCheckbox(selected: Boolean, onChecked: () -> Unit) {
    val checkboxIcon = when (selected) {
        false -> Icons.Rounded.RadioButtonUnchecked
        true -> Icons.Rounded.Check
    }

    val checkboxIconColor = when (selected) {
        false -> SubtitleGrey
        true -> MaterialTheme.colors.primary
    }

    Icon(
        imageVector = checkboxIcon,
        tint = checkboxIconColor,
        contentDescription = stringResource(R.string.cd_checkbox_complete_reminder),
        modifier = Modifier.clickable { onChecked() }
    )
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun OverdueReminderListItemPreview() {
    RemindMeTheme {
        val reminderState = ReminderState(
            id = 1,
            name = "Yoga with Alice",
            date = "Wed, 14 Mar 2022",
            time = LocalTime.of(14, 30),
            isNotificationSent = true,
            isRepeatReminder = true,
            repeatAmount = "2",
            repeatUnit = "Weeks",
            notes = "Don't forget to warm up!"
        )

        OverdueReminderListItem(reminderState = reminderState, {})
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ScheduledReminderListItemPreview() {
    RemindMeTheme {
        val reminderState = ReminderState(
            id = 1,
            name = "Yoga with Alice",
            date = "Wed, 14 Mar 2022",
            time = LocalTime.of(14, 30),
            isNotificationSent = true,
            isRepeatReminder = true,
            repeatAmount = "2",
            repeatUnit = "Weeks",
            notes = "Don't forget to warm up!"
        )

        ScheduledReminderListItem(reminderState = reminderState)
    }
}
