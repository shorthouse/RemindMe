package dev.shorthouse.remindme.compose.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.TimePickerDefaults
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.state.PreviewData
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.theme.Black
import dev.shorthouse.remindme.theme.RemindMeTheme
import dev.shorthouse.remindme.theme.SubtitleGreyLighter
import dev.shorthouse.remindme.theme.White
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ReminderAlertDialog(
    title: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.body1
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = confirmText,
                    style = MaterialTheme.typography.button,
                    color = MaterialTheme.colors.primary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.dialog_action_cancel),
                    style = MaterialTheme.typography.button,
                    color = MaterialTheme.colors.primary
                )
            }
        },
        onDismissRequest = onDismiss,
    )
}

@Composable
fun ReminderDatePickerDialog(
    reminderState: ReminderState,
    dialogState: MaterialDialogState
) {
    MaterialDialog(
        dialogState = dialogState,
        buttons = {
            positiveButton(
                text = stringResource(R.string.dialog_action_ok),
                textStyle = MaterialTheme.typography.button
            )
            negativeButton(
                text = stringResource(R.string.dialog_action_cancel),
                textStyle = MaterialTheme.typography.button
            )
        },
    ) {
        datepicker(
            initialDate = LocalDate.now(),
            title = "",
            onDateChange = { reminderState.date = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy").format(it) },
            allowedDateValidator = { it.isAfter(LocalDate.now().minusDays(1)) }
        )
    }
}

@Composable
fun ReminderTimePickerDialog(
    reminderState: ReminderState,
    dialogState: MaterialDialogState
) {
    MaterialDialog(
        dialogState = dialogState,
        buttons = {
            positiveButton(
                text = stringResource(R.string.dialog_action_ok),
                textStyle = MaterialTheme.typography.button
            )
            negativeButton(
                text = stringResource(R.string.dialog_action_cancel),
                textStyle = MaterialTheme.typography.button
            )
        },
    ) {
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_normal)))

        timepicker(
            initialTime = reminderState.time,
            title = "",
            is24HourClock = true,
            onTimeChange = { reminderState.time = it },
            colors = TimePickerDefaults.colors(
                activeBackgroundColor = MaterialTheme.colors.primary,
                activeTextColor = White,
                inactiveBackgroundColor = SubtitleGreyLighter,
                inactiveTextColor = Black,
                selectorColor = MaterialTheme.colors.primary,
                selectorTextColor = White,
            )
        )
    }
}

@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ReminderAlertDialogPreview() {
    RemindMeTheme {
        ReminderAlertDialog(
            title = "Do this action?",
            confirmText = "Confirm",
            onConfirm = {},
            onDismiss = {}
        )
    }
}

@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ReminderDatePickerDialogPreview() {
    RemindMeTheme {
        ReminderDatePickerDialog(
            reminderState = PreviewData.reminderState,
            dialogState = MaterialDialogState(initialValue = true)
        )
    }
}

@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ReminderTimePickerDialogPreview() {
    RemindMeTheme {
        ReminderTimePickerDialog(
            reminderState = PreviewData.reminderState,
            dialogState = MaterialDialogState(initialValue = true)
        )
    }
}
