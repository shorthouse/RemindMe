package dev.shorthouse.remindme.ui.list

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notes
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.ui.previewprovider.ReminderCardProvider
import dev.shorthouse.remindme.ui.theme.AppTheme
import dev.shorthouse.remindme.ui.theme.Blue
import dev.shorthouse.remindme.ui.theme.Green
import dev.shorthouse.remindme.ui.theme.Red
import java.time.temporal.ChronoUnit

@Composable
fun ReminderCard(
    reminder: Reminder,
    onReminderCard: (Reminder) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shadowElevation = 4.dp,
        tonalElevation = 0.dp,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        onClick = { onReminderCard(reminder) },
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = reminder.name,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = stringResource(
                    R.string.reminder_card_date_time,
                    reminder.getFormattedDate(),
                    reminder.getFormattedTime()
                )
            )

            Spacer(Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ReminderCardStatus(reminder = reminder)

                if (reminder.isNotificationSent) {
                    Icon(
                        imageVector = Icons.Rounded.NotificationsNone,
                        tint = MaterialTheme.colorScheme.outline,
                        contentDescription = stringResource(R.string.cd_details_notification),
                        modifier = Modifier.size(20.dp)
                    )
                }

                reminder.repeatInterval?.let { repeatInterval ->
                    val pluralId = when (repeatInterval.unit) {
                        ChronoUnit.DAYS -> R.plurals.repeat_interval_days
                        else -> R.plurals.repeat_interval_days
                    }

                    ReminderCardDetail(
                        icon = Icons.Rounded.Repeat,
                        text = pluralStringResource(
                            pluralId,
                            repeatInterval.amount.toInt(),
                            repeatInterval.amount
                        ),
                        contentDescription = stringResource(R.string.cd_details_repeat_interval)
                    )
                }
            }

            reminder.notes?.let { notes ->
                Spacer(Modifier.height(12.dp))

                ReminderCardDetail(
                    icon = Icons.Rounded.Notes,
                    text = notes,
                    contentDescription = stringResource(R.string.cd_details_notes)
                )
            }
        }
    }
}

@Composable
private fun ReminderCardStatus(
    reminder: Reminder,
    modifier: Modifier = Modifier
) {
    val statusText = when {
        reminder.isCompleted -> stringResource(R.string.reminder_completed)
        reminder.isOverdue() -> stringResource(R.string.reminder_overdue)
        else -> stringResource(R.string.reminder_upcoming)
    }

    val statusBackgroundColor = when {
        reminder.isCompleted -> Green
        reminder.isOverdue() -> Red
        else -> Blue
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = statusBackgroundColor,
        modifier = modifier
    ) {
        Text(
            text = statusText,
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 6.dp)
        )
    }
}

@Composable
private fun ReminderCardDetail(
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
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(20.dp)
        )

        Spacer(Modifier.width(8.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
fun ReminderCardPreview(
    @PreviewParameter(ReminderCardProvider::class) reminder: Reminder
) {
    AppTheme {
        ReminderCard(
            reminder = reminder,
            onReminderCard = {}
        )
    }
}
