package dev.shorthouse.remindme.compose.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.TimePickerDefaults
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.preview.PreviewData
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.theme.Black
import dev.shorthouse.remindme.theme.RemindMeTheme
import dev.shorthouse.remindme.theme.SubtitleGreyLighter
import dev.shorthouse.remindme.theme.White
import dev.shorthouse.remindme.util.enums.ReminderSortOrder
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ReminderSortDialog(
    initialSort: ReminderSortOrder,
    onApplySort: (ReminderSortOrder) -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedSortOption by remember { mutableStateOf(initialSort) }
    val sortOptions = ReminderSortOrder.values()

    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismiss
    ) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colors.surface)
                    .padding(dimensionResource(R.dimen.margin_small))
                    .fillMaxWidth(0.9f)
            ) {
                Text(
                    text = stringResource(R.string.sort_dialog_title),
                    style = MaterialTheme.typography.h5,
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    ),
                    color = MaterialTheme.colors.onSurface
                )

                sortOptions.forEach { sortOption ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RadioButton(
                            selected = (sortOption == selectedSortOption),
                            onClick = { selectedSortOption = sortOption }
                        )

                        Text(
                            text = stringResource(sortOption.displayNameStringId),
                            style = MaterialTheme.typography.body1,
                            color = MaterialTheme.colors.onSurface
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = {
                        onApplySort(selectedSortOption)
                        onDismiss()
                    }) {
                        Text(
                            text = stringResource(R.string.dialog_action_apply),
                            style = MaterialTheme.typography.button,
                            color = MaterialTheme.colors.primary,
                        )
                    }
                }
            }
        }
    }
}

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
fun ReminderSortDialogPreview() {
    RemindMeTheme {
        ReminderSortDialog(
            initialSort = ReminderSortOrder.EARLIEST_DATE_FIRST,
            onApplySort = {},
            onDismiss = {}
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
            reminderState = PreviewData.previewReminderState,
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
            reminderState = PreviewData.previewReminderState,
            dialogState = MaterialDialogState(initialValue = true)
        )
    }
}
