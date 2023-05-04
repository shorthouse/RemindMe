package dev.shorthouse.remindme.ui.list

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TextSnippet
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Loop
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ReminderCard(
    reminder: Reminder,
    onReminderCard: (Reminder) -> Unit,
    onCompleteReminder: (Reminder) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shadowElevation = 4.dp,
        tonalElevation = 0.dp,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        onClick = { onReminderCard(reminder) },
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            Divider(
                color = when {
                    reminder.isCompleted -> Green
                    reminder.isOverdue -> Red
                    else -> Blue
                },
                modifier = Modifier
                    .fillMaxHeight()
                    .width(8.dp)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Text(
                    text = reminder.name,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = stringResource(
                        R.string.reminder_card_date_time,
                        reminder.formattedDate,
                        reminder.formattedTime
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )

                if (reminder.hasOptionalParts()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        val tint = MaterialTheme.colorScheme.onSurfaceVariant
                        val sizeModifier = Modifier.size(20.dp)

                        if (reminder.isNotificationSent) {
                            Icon(
                                imageVector = Icons.Rounded.NotificationsNone,
                                contentDescription = stringResource(
                                    R.string.cd_card_notification_enabled
                                ),
                                tint = tint,
                                modifier = sizeModifier
                            )
                        }

                        reminder.repeatInterval?.let {
                            Icon(
                                imageVector = Icons.Rounded.Loop,
                                contentDescription = stringResource(
                                    R.string.cd_card_repeat_reminder
                                ),
                                tint = tint,
                                modifier = sizeModifier
                            )
                        }

                        reminder.notes?.let {
                            Icon(
                                imageVector = Icons.Outlined.TextSnippet,
                                contentDescription = stringResource(
                                    R.string.cd_card_has_notes
                                ),
                                tint = tint,
                                modifier = sizeModifier
                            )
                        }
                    }
                }
            }

            var isReminderCompleted by remember { mutableStateOf(false) }
            val coroutineScope = rememberCoroutineScope()

            if (!reminder.isCompleted) {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            isReminderCompleted = true
                            delay(350.milliseconds)
                            onCompleteReminder(reminder)
                            if (reminder.isRepeatReminder) isReminderCompleted = false
                        }
                    },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(
                        imageVector = if (isReminderCompleted) {
                            Icons.Rounded.Done
                        } else {
                            Icons.Rounded.RadioButtonUnchecked
                        },
                        tint = if (isReminderCompleted) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        contentDescription = stringResource(R.string.cd_complete_reminder)
                    )
                }
            }
        }
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
            onCompleteReminder = {},
            onReminderCard = {}
        )
    }
}
