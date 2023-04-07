package dev.shorthouse.remindme.ui.component.dialog

import android.content.res.Configuration
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.ui.theme.AppTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderDatePicker(
    initialDate: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("EEE, dd MMM yyyy") }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = dateStringToEpochMillis(initialDate, dateFormatter)
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { utcMillis ->
                    onConfirm(epochMillisToDateString(utcMillis, dateFormatter))
                    onDismiss()
                }
            }) {
                Text(text = stringResource(R.string.dialog_action_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.dialog_action_cancel))
            }
        },
        tonalElevation = 0.dp,
        modifier = modifier
    ) {
        DatePicker(
            state = datePickerState,
            dateValidator = { utcMillis ->
                val day = Instant.ofEpochMilli(utcMillis)
                    .atZone(ZoneId.of("UTC"))
                    .withZoneSameInstant(ZoneId.systemDefault())

                val yesterday = ZonedDateTime.now().minusDays(1)

                day.isAfter(yesterday)
            },
            showModeToggle = false
        )
    }
}

private fun dateStringToEpochMillis(date: String, dateFormatter: DateTimeFormatter): Long {
    return LocalDate.parse(date, dateFormatter)
        .atStartOfDay()
        .plusDays(1)
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}

private fun epochMillisToDateString(utcMillis: Long, dateFormatter: DateTimeFormatter): String {
    return Instant.ofEpochMilli(utcMillis)
        .atZone(ZoneId.of("UTC"))
        .withZoneSameInstant(ZoneId.systemDefault())
        .toLocalDate()
        .format(dateFormatter)
        .toString()
}

@Composable
@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
fun DatePickerDialogPreview() {
    AppTheme {
        ReminderDatePicker(
            initialDate = "Wed, 01 Jan 2020",
            onConfirm = {},
            onDismiss = {}
        )
    }
}