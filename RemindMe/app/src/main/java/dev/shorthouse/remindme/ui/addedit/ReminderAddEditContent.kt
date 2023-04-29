package dev.shorthouse.remindme.ui.addedit

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TextSnippet
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.ui.component.chip.RemindMeInputChip
import dev.shorthouse.remindme.ui.component.dialog.ReminderDatePicker
import dev.shorthouse.remindme.ui.component.dialog.ReminderTimePicker
import dev.shorthouse.remindme.ui.component.dialog.RepeatIntervalDialog
import dev.shorthouse.remindme.ui.component.text.RemindMeTextField
import dev.shorthouse.remindme.ui.previewprovider.DefaultReminderProvider
import dev.shorthouse.remindme.ui.theme.AppTheme
import java.time.temporal.ChronoUnit

@Composable
fun ReminderAddEditContent(
    reminder: Reminder,
    onHandleEvent: (ReminderAddEditEvent) -> Unit,
    onNavigateUp: () -> Unit,
    isReminderValid: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        RemindMeTextField(
            text = reminder.name,
            onTextChange = {
                onHandleEvent(ReminderAddEditEvent.UpdateName(it))
            },
            textStyle = MaterialTheme.typography.titleLarge,
            hintText = stringResource(R.string.hint_reminder_name),
            imeAction = ImeAction.Done,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp)
        )

        var isDatePickerShown by remember { mutableStateOf(false) }

        if (isDatePickerShown) {
            ReminderDatePicker(
                initialDate = reminder.startDateTime.toLocalDate(),
                onConfirm = { onHandleEvent(ReminderAddEditEvent.UpdateDate(it)) },
                onDismiss = { isDatePickerShown = false }
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { isDatePickerShown = true }
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.CalendarToday,
                tint = MaterialTheme.colorScheme.outline,
                contentDescription = stringResource(R.string.cd_details_date)
            )

            Spacer(Modifier.width(16.dp))

            Text(
                text = reminder.formattedDate,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        var isTimePickerShown by remember { mutableStateOf(false) }
        if (isTimePickerShown) {
            ReminderTimePicker(
                initialTime = reminder.startDateTime.toLocalTime(),
                onConfirm = { onHandleEvent(ReminderAddEditEvent.UpdateTime(it)) },
                onDismiss = { isTimePickerShown = false }
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { isTimePickerShown = true }
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Schedule,
                tint = MaterialTheme.colorScheme.outline,
                contentDescription = stringResource(R.string.cd_details_time)
            )

            Spacer(Modifier.width(16.dp))

            Text(
                text = reminder.formattedTime,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.TextSnippet,
                contentDescription = stringResource(R.string.cd_details_notes),
                tint = MaterialTheme.colorScheme.outline
            )

            Spacer(Modifier.width(16.dp))

            RemindMeTextField(
                text = reminder.notes.orEmpty(),
                onTextChange = { onHandleEvent(ReminderAddEditEvent.UpdateNotes(it)) },
                textStyle = MaterialTheme.typography.bodyMedium,
                hintText = stringResource(R.string.hint_reminder_notes),
                imeAction = ImeAction.Done,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Row(modifier = Modifier.padding(vertical = 8.dp)) {
            RemindMeInputChip(
                selected = reminder.isNotificationSent,
                onClick = {
                    onHandleEvent(
                        ReminderAddEditEvent.UpdateNotification(!reminder.isNotificationSent)
                    )
                },
                label = {
                    val labelText = if (reminder.isNotificationSent) {
                        stringResource(R.string.reminder_details_notification_on)
                    } else {
                        stringResource(R.string.reminder_details_add_notification)
                    }

                    Text(
                        text = labelText,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.NotificationsNone,
                        contentDescription = null
                    )
                },
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.width(8.dp))

            var isRepeatIntervalDialogShown by remember { mutableStateOf(false) }
            RemindMeInputChip(
                selected = reminder.repeatInterval != null,
                onClick = { isRepeatIntervalDialogShown = true },
                label = {
                    val labelText = if (reminder.repeatInterval == null) {
                        stringResource(R.string.reminder_details_add_repeat)
                    } else {
                        val pluralId = when (reminder.repeatInterval.unit) {
                            ChronoUnit.DAYS -> R.plurals.repeat_interval_days
                            else -> R.plurals.repeat_interval_weeks
                        }

                        pluralStringResource(
                            pluralId,
                            reminder.repeatInterval.amount,
                            reminder.repeatInterval.amount
                        )
                    }

                    Text(
                        text = labelText,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .padding(vertical = 12.dp)
                            .weight(1f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    reminder.repeatInterval?.let {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                onHandleEvent(ReminderAddEditEvent.UpdateRepeatInterval(null))
                            }
                        )
                    }
                },
                modifier = Modifier.weight(1f)
            )

            if (isRepeatIntervalDialogShown) {
                RepeatIntervalDialog(
                    initialRepeatInterval = reminder.repeatInterval,
                    onConfirm = {
                        onHandleEvent(ReminderAddEditEvent.UpdateRepeatInterval(it))
                    },
                    onDismiss = { isRepeatIntervalDialogShown = false }
                )
            }
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = {
                onHandleEvent(ReminderAddEditEvent.SaveReminder(reminder))
                onNavigateUp()
            },
            content = {
                Text(
                    text = stringResource(R.string.button_save_reminder),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 1.dp)
                )
            },
            enabled = isReminderValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, top = 8.dp)
        )
    }
}

@Composable
@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
fun ReminderAddContentPreview() {
    AppTheme {
        ReminderAddEditContent(
            reminder = Reminder(),
            onHandleEvent = {},
            onNavigateUp = {},
            isReminderValid = false
        )
    }
}

@Composable
@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
fun ReminderEditContentPreview(
    @PreviewParameter(DefaultReminderProvider::class) reminder: Reminder
) {
    AppTheme {
        ReminderAddEditContent(
            reminder = reminder,
            onHandleEvent = {},
            onNavigateUp = {},
            isReminderValid = true
        )
    }
}
