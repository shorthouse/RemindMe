package dev.shorthouse.remindme.ui.component.dialog

import android.content.res.Configuration
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.ui.theme.AppTheme
import java.time.LocalTime

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ReminderTimePicker(
    initialTime: LocalTime,
    onConfirm: (LocalTime) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = true
    )

    RemindMeAlertDialog(
        title = null,
        content = {
            TimePicker(state = timePickerState)
        },
        confirmText = stringResource(R.string.dialog_action_ok),
        onConfirm = {
            onConfirm(LocalTime.of(timePickerState.hour, timePickerState.minute))
            onDismiss()
        },
        onDismiss = onDismiss,
        modifier = modifier
    )
}

@Composable
@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun TimePickerDialogPreview() {
    AppTheme {
        ReminderTimePicker(
            initialTime = LocalTime.now(),
            onConfirm = {},
            onDismiss = {}
        )
    }
}
