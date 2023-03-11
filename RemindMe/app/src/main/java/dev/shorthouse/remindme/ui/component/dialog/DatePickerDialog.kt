package dev.shorthouse.remindme.ui.component.dialog

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.ui.theme.m3.AppTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    initialDate: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = dateStringToEpochMillis(initialDate)
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.extraLarge) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
//                modifier = Modifier
//                    .padding(dimensionResource(R.dimen.margin_normal))
            ) {
                DatePicker(
                    state = datePickerState,
                    dateValidator = { timestamp ->
                        timestamp >= ZonedDateTime.now().toInstant().toEpochMilli()
                    }
                )

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(text = stringResource(R.string.dialog_action_cancel))
                    }

                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { timestamp ->
                            onConfirm(epochMillisToDateString(timestamp))
                            onDismiss()
                        }
                    }) {
                        Text(text = stringResource(R.string.dialog_action_ok))
                    }
                }
            }
        }
    }
}

private fun dateStringToEpochMillis(date: String): Long {
    val dateFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy")

    return LocalDate.parse(date, dateFormatter)
        .atStartOfDay()
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}

private fun epochMillisToDateString(millis: Long): String {
    val dateFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy")

    return Instant.ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(dateFormatter)
        .toString()
}

@Composable
@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
fun DatePickerDialogPreview() {
    AppTheme {
        DatePickerDialog(
            initialDate = LocalDate.now().toString(),
            onConfirm = {},
            onDismiss = {}
        )
    }
}
