package dev.shorthouse.remindme.compose.component

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.model.Reminder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalTime
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun ActiveReminderListItem(
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
fun AllReminderListItem(
    reminderState: ReminderState,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        ReminderListItem(reminderState = reminderState)
    }
}

@Composable
fun ReminderListItem(reminderState: ReminderState, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = reminderState.name,
            color = colorResource(R.color.text_on_surface),
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.margin_tiny)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = reminderState.date,
                color = colorResource(R.color.subtitle_grey),
                fontSize = 14.sp
            )

            Text(
                text = reminderState.time.toString(),
                color = colorResource(R.color.subtitle_grey),
                fontSize = 14.sp
            )

            if (reminderState.isNotificationSent) {
                Icon(
                    painter = painterResource(R.drawable.ic_notification_outline),
                    contentDescription = stringResource(R.string.cd_notification_sent),
                    tint = colorResource(R.color.subtitle_grey),
                    modifier = Modifier.size(dimensionResource(R.dimen.margin_normal))
                )
            }

            if (reminderState.isRepeatReminder) {
                Icon(
                    painter = painterResource(R.drawable.ic_repeat),
                    contentDescription = stringResource(R.string.cd_repeat_reminder),
                    tint = colorResource(R.color.subtitle_grey),
                    modifier = Modifier.size(dimensionResource(R.dimen.margin_normal))
                )
            }
        }

    }
}

@Composable
fun ReminderListItemCheckbox(selected: Boolean, onChecked: () -> Unit) {
    val checkboxIcon = when (selected) {
        false -> painterResource(R.drawable.ic_checkbox_circle)
        true -> painterResource(R.drawable.ic_check)
    }

    val checkboxIconColor = when (selected) {
        false -> colorResource(R.color.subtitle_grey)
        true -> colorResource(R.color.blue)
    }

    Icon(
        painter = checkboxIcon,
        tint = checkboxIconColor,
        contentDescription = stringResource(R.string.cd_checkbox_complete_reminder),
        modifier = Modifier.clickable { onChecked() }
    )
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ActiveReminderListItemPreview() {
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

    ActiveReminderListItem(reminderState = reminderState, {})
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun AllReminderListItemPreview() {
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

    AllReminderListItem(reminderState = reminderState)
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ReminderListItemPreview() {
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

    ReminderListItem(reminderState = reminderState)
}
