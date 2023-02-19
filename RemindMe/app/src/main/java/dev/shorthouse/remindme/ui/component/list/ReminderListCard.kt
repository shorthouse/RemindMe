package dev.shorthouse.remindme.ui.component.list

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notes
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.ui.preview.ReminderListCardProvider
import dev.shorthouse.remindme.ui.state.ReminderState
import dev.shorthouse.remindme.ui.theme.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ReminderListCard(
    reminderState: ReminderState,
    onReminderCard: (ReminderState) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(dimensionResource(R.dimen.margin_small)),
        elevation = dimensionResource(R.dimen.reminder_list_card_elevation),
        onClick = { onReminderCard(reminderState) },
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
            )

            Text(
                text = stringResource(
                    R.string.reminder_list_card_date_time,
                    reminderState.date,
                    reminderState.time
                )
            )

            Spacer(Modifier.height(dimensionResource(R.dimen.margin_small)))

            Row(
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.margin_small)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ReminderCardStatus(reminderState = reminderState)

                if (reminderState.isNotificationSent) {
                    Icon(
                        imageVector = Icons.Rounded.NotificationsNone,
                        tint = SubtitleGrey,
                        contentDescription = stringResource(R.string.cd_details_notification),
                        modifier = Modifier.size(dimensionResource(R.dimen.reminder_list_card_icon_size))
                    )
                }

                if (reminderState.isRepeatReminder) {
                    ReminderListCardDetail(
                        icon = Icons.Rounded.Repeat,
                        text = stringResource(
                            R.string.reminder_details_repeat_interval,
                            reminderState.repeatAmount,
                            reminderState.repeatUnit.lowercase()
                        ),
                        contentDescription = stringResource(R.string.cd_details_repeat_interval),
                    )
                }
            }

            reminderState.notes?.let { notes ->
                Spacer(Modifier.height(dimensionResource(R.dimen.margin_small)))

                ReminderListCardDetail(
                    icon = Icons.Rounded.Notes,
                    text = notes,
                    contentDescription = stringResource(R.string.cd_details_notes)
                )
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
            modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.reminder_list_card_status_padding))
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
            modifier = Modifier.size(dimensionResource(R.dimen.reminder_list_card_icon_size))
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
fun ReminderListCardPreview(
    @PreviewParameter(ReminderListCardProvider::class) reminderState: ReminderState
) {
    RemindMeTheme {
        ReminderListCard(
            reminderState = reminderState,
            onReminderCard = {}
        )
    }
}
