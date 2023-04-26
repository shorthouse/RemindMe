package dev.shorthouse.remindme.ui.component.dialog

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.ui.state.ReminderState
import dev.shorthouse.remindme.ui.theme.AppTheme

@Composable
fun RepeatIntervalDialog(
    reminderState: ReminderState,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            modifier = modifier
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(0.9f)
            ) {
                Text(
                    text = "Set repeat interval",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    )
                )

                RepeatIntervalDialogContent(reminderState)

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                ) {
                    TextButton(onClick = { onDismiss() }) {
                        Text(
                            text = stringResource(R.string.dialog_action_apply),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RepeatIntervalDialogContent(reminderState: ReminderState) {
    val repeatAmount = reminderState.repeatAmount.toIntOrNull() ?: 0

    reminderState.repeatUnit = when {
        stringResource(R.string.day) in reminderState.repeatUnit -> pluralStringResource(
            R.plurals.repeat_unit_days,
            repeatAmount
        )
        else -> pluralStringResource(
            R.plurals.repeat_unit_weeks,
            repeatAmount
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedTextField(
                    value = reminderState.repeatAmount,
                    onValueChange = { repeatAmount ->
                        if (repeatAmount.length <= 2) {
                            reminderState.repeatAmount = repeatAmount
                                .trimStart { it == '0' }
                                .filter { it.isDigit() }
                        }
                    },
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        textAlign = TextAlign.Center
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier
                        .width(72.dp)
                        .padding(end = 16.dp)
                        .testTag(stringResource(R.string.test_tag_text_field_repeat_amount))
                )
            }

            Spacer(Modifier.width(24.dp))

            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.Start
            ) {
                val repeatUnitPluralIds = listOf(
                    R.plurals.repeat_unit_days,
                    R.plurals.repeat_unit_weeks
                )
                val repeatAmount = reminderState.repeatAmount.toIntOrNull() ?: 0

                val repeatUnitOptions = repeatUnitPluralIds.map { pluralId ->
                    pluralStringResource(
                        pluralId,
                        repeatAmount
                    )
                }

                Column {
                    repeatUnitOptions.forEach { text ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .selectable(
                                    selected = (text == reminderState.repeatUnit),
                                    onClick = { reminderState.repeatUnit = text }
                                )
                                .fillMaxWidth(0.8f)
                        ) {
                            RadioButton(
                                selected = (text == reminderState.repeatUnit),
                                onClick = { reminderState.repeatUnit = text },
                                modifier = Modifier.testTag(text)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
fun RepeatIntervalDialogPreview() {
    AppTheme {
        RepeatIntervalDialog(
            reminderState = ReminderState(),
            onConfirm = {},
            onDismiss = {}
        )
    }
}
