package dev.shorthouse.remindme.compose.component.dialog

import android.content.res.Configuration
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.previewdata.PreviewData
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.theme.RemindMeTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DatePickerDialog(
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

@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DatePickerDialogPreview() {
    RemindMeTheme {
        DatePickerDialog(
            reminderState = PreviewData.previewReminderState,
            dialogState = MaterialDialogState(initialValue = true)
        )
    }
}
