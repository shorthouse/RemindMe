package dev.shorthouse.remindme.compose.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.TimePickerDefaults
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.state.ReminderState
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
            Text(text = title, fontSize = 18.sp)
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = confirmText, fontSize = 16.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.alert_dialog_cancel), fontSize = 16.sp)
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
    val dialogButtonTextStyle = TextStyle(
        color = colorResource(R.color.on_surface),
        fontWeight = FontWeight.Bold
    )

    MaterialDialog(
        dialogState = dialogState,
        buttons = {
            positiveButton(
                text = stringResource(R.string.dialog_ok),
                textStyle = dialogButtonTextStyle
            )
            negativeButton(
                text = stringResource(R.string.dialog_cancel),
                textStyle = dialogButtonTextStyle
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
    val dialogButtonTextStyle = TextStyle(
        color = colorResource(R.color.on_surface),
        fontWeight = FontWeight.Bold
    )

    MaterialDialog(
        dialogState = dialogState,
        buttons = {
            positiveButton(
                text = stringResource(R.string.dialog_ok),
                textStyle = dialogButtonTextStyle
            )
            negativeButton(
                text = stringResource(R.string.dialog_cancel),
                textStyle = dialogButtonTextStyle
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
                activeBackgroundColor = colorResource(R.color.blue),
                inactiveBackgroundColor = Color.LightGray,
                inactiveTextColor = Color.Black,
                selectorColor = colorResource(R.color.blue),
            ),
        )
    }
}
