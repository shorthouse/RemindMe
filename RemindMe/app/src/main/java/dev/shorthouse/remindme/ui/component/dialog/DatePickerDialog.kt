package dev.shorthouse.remindme.ui.component.dialog

import android.content.res.Configuration
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.ui.theme.m2.RemindMeTheme
import java.time.LocalDate

@Composable
fun DatePickerDialog(
    date: LocalDate,
    onDateChange: (LocalDate) -> Unit,
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
        }
    ) {
        datepicker(
            initialDate = date,
            title = "",
            onDateChange = onDateChange,
            allowedDateValidator = { it.isAfter(LocalDate.now().minusDays(1)) }
        )
    }
}

@Composable
@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
fun DatePickerDialogPreview() {
    RemindMeTheme {
        val dialogState = MaterialDialogState(initialValue = true)

        DatePickerDialog(
            date = LocalDate.now(),
            onDateChange = {},
            dialogState = dialogState
        )
    }
}
