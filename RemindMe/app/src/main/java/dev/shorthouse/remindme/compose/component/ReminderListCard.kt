package dev.shorthouse.remindme.compose.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notes
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.preview.PreviewData
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.theme.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ReminderListCard(
    reminderState: ReminderState,
    onReminderActions: (ReminderState) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(dimensionResource(R.dimen.margin_small)),
        onClick = { onReminderActions(reminderState) },
        elevation = 4.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(dimensionResource(R.dimen.margin_normal))
                .fillMaxWidth()
        ) {
            Text(
                text = reminderState.name,
                style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colors.onSurface,
            )

            Text(
                text = stringResource(R.string.reminder_list_card_date_time, reminderState.date, reminderState.time),
                style = MaterialTheme.typography.subtitle2
            )


            Divider(
                color = SubtitleGrey,
                thickness = 1.dp,
                modifier = Modifier.padding(
                    top = dimensionResource(R.dimen.margin_small),
                    bottom = dimensionResource(R.dimen.margin_large),
                )
            )


            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ReminderCardStatus(reminderState = reminderState)

                val notificationText = if (reminderState.isNotificationSent) {
                    stringResource(R.string.reminder_card_notification_on)
                } else {
                    stringResource(R.string.reminder_card_notification_off)
                }

                ReminderListCardDetail(
                    icon = Icons.Rounded.NotificationsNone,
                    text = notificationText,
                    contentDescription = stringResource(R.string.cd_details_notification),
                )

                if (reminderState.isRepeatReminder) {
                    ReminderListCardDetail(
                        icon = Icons.Rounded.Refresh,
                        text = stringResource(
                            R.string.reminder_details_repeat_interval,
                            reminderState.repeatAmount,
                            reminderState.repeatUnit.lowercase()
                        ),
                        contentDescription = stringResource(R.string.cd_details_repeat_interval),
                    )
                }

                reminderState.notes?.let { notes ->
                    ReminderListCardDetail(
                        icon = Icons.Rounded.Notes,
                        text = notes,
                        contentDescription = stringResource(R.string.cd_details_notes)
                    )
                }
            }
        }
    }
}

@Composable
private fun ReminderCardStatus(reminderState: ReminderState) {
    val statusText = when {
        reminderState.isCompleted -> stringResource(R.string.reminder_status_completed)
        reminderState.isOverdue() -> stringResource(R.string.reminder_status_overdue)
        else -> stringResource(R.string.reminder_status_scheduled)
    }

    val statusBackgroundColor = when {
        reminderState.isCompleted -> Green
        reminderState.isOverdue() -> Red
        else -> Blue500
    }

    Surface(
        shape = RoundedCornerShape(dimensionResource(R.dimen.margin_tiny)),
        color = statusBackgroundColor,
    ) {
        Text(
            text = statusText,
            style = MaterialTheme.typography.subtitle2,
            color = MaterialTheme.colors.onPrimary,
            modifier = Modifier.padding(
                horizontal = dimensionResource(R.dimen.margin_tiny),
                vertical = dimensionResource(R.dimen.margin_reminder_status_chip),
            )
        )
    }
}

@Composable
private fun ReminderListCardDetail(
    icon: ImageVector,
    text: String,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = SubtitleGrey,
        )

        Spacer(Modifier.width(dimensionResource(R.dimen.margin_tiny)))

        Text(
            text = text,
            style = MaterialTheme.typography.subtitle2,
            color = MaterialTheme.colors.onBackground,
        )
    }
}

@Composable
@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
fun ReminderListCardPreview() {
    RemindMeTheme {
        val reminderState = PreviewData.previewReminderState

        ReminderListCard(
            reminderState = reminderState,
            onReminderActions = {}
        )
    }
}
