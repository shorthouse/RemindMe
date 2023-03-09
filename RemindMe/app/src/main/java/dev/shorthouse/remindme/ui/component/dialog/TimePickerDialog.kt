package dev.shorthouse.remindme.ui.component.dialog

import android.app.TimePickerDialog
import android.content.res.Configuration
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import dev.shorthouse.remindme.ui.theme.m3.AppTheme
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Locale
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialogX(
    time: LocalTime,
    onTimeChange: (LocalTime) -> Unit,
    dialogState: MaterialDialogState
) {
    var showTimePicker by remember { mutableStateOf(false) }
    val state = rememberTimePickerState()
    val formatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val snackState = remember { SnackbarHostState() }
    val snackScope = rememberCoroutineScope()


    Dialog(
        onDismissRequest = { showTimePicker = false},
        content = {
            TimePicker(state = state)

        }
    )
//    }
//    TimePickerDialog(
//        onCancel = { showTimePicker = false },
//        onConfirm = {
//            val cal = Calendar.getInstance()
//            cal.set(Calendar.HOUR_OF_DAY, state.hour)
//            cal.set(Calendar.MINUTE, state.minute)
//            cal.isLenient = false
//            snackScope.launch {
//                snackState.showSnackbar("Entered time: ${formatter.format(cal.time)}")
//            }
//            showTimePicker = false
//        }
//    ) {
//        TimePicker(state = state)
//    }

//    Box(propagateMinConstraints = false) {
//        Button(
//            modifier = Modifier.align(Alignment.Center),
//            onClick = { showTimePicker = true }
//        ) { Text("Set Time") }
//        SnackbarHost(hostState = snackState)
//    }

//    if (showTimePicker) {
//        TimePickerDialog(
//            onCancel = { showTimePicker = false },
//            onConfirm = {
//                val cal = Calendar.getInstance()
//                cal.set(Calendar.HOUR_OF_DAY, state.hour)
//                cal.set(Calendar.MINUTE, state.minute)
//                cal.isLenient = false
//                snackScope.launch {
//                    snackState.showSnackbar("Entered time: ${formatter.format(cal.time)}")
//                }
//                showTimePicker = false
//            }
//        ) {
//            TimePicker(state = state)
//        }
//    }
}

// @Composable
// fun TimePickerDialogOld(
//    time: LocalTime,
//    onTimeChange: (LocalTime) -> Unit,
//    dialogState: MaterialDialogState
// ) {
//    MaterialDialog(
//        dialogState = dialogState,
//        buttons = {
//            positiveButton(
//                text = stringResource(R.string.dialog_action_ok),
//                textStyle = MaterialTheme.typography.button
//            )
//            negativeButton(
//                text = stringResource(R.string.dialog_action_cancel),
//                textStyle = MaterialTheme.typography.button
//            )
//        }
//    ){
//        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_normal)))
//
//        timepicker(
//            initialTime = time,
//            title = "",
//            is24HourClock = true,
//            onTimeChange = onTimeChange,
//            colors = TimePickerDefaults.colors(
//                activeBackgroundColor = MaterialTheme.colors.primary,
//                activeTextColor = White,
//                inactiveBackgroundColor = LightGrey,
//                inactiveTextColor = Black,
//                selectorColor = MaterialTheme.colors.primary,
//                selectorTextColor = White
//            )
//        )
//    }
// }

@Composable
@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
fun TimePickerDialogPreview() {
    AppTheme {
        val dialogState = MaterialDialogState(initialValue = true)

        TimePickerDialog(
            time = LocalTime.now(),
            onTimeChange = {},
            dialogState = dialogState
        )
    }
}
