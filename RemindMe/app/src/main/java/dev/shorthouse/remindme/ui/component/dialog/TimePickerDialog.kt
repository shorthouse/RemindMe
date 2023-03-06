package dev.shorthouse.remindme.ui.component.dialog

import android.content.res.Configuration
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.time.TimePickerDefaults
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.ui.theme.m2.Black
import dev.shorthouse.remindme.ui.theme.m2.LightGrey
import dev.shorthouse.remindme.ui.theme.m2.RemindMeTheme
import dev.shorthouse.remindme.ui.theme.m2.White
import java.time.LocalTime

@Composable
fun TimePickerDialog(
    time: LocalTime,
    onTimeChange: (LocalTime) -> Unit,
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
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_normal)))

        timepicker(
            initialTime = time,
            title = "",
            is24HourClock = true,
            onTimeChange = onTimeChange,
            colors = TimePickerDefaults.colors(
                activeBackgroundColor = MaterialTheme.colors.primary,
                activeTextColor = White,
                inactiveBackgroundColor = LightGrey,
                inactiveTextColor = Black,
                selectorColor = MaterialTheme.colors.primary,
                selectorTextColor = White
            )
        )
    }
}

@Composable
@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
fun TimePickerDialogPreview() {
    RemindMeTheme {
        val dialogState = MaterialDialogState(initialValue = true)

        TimePickerDialog(
            time = LocalTime.now(),
            onTimeChange = {},
            dialogState = dialogState
        )
    }
}
