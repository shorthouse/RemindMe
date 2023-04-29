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
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.model.RepeatInterval
import dev.shorthouse.remindme.ui.theme.AppTheme
import java.time.temporal.ChronoUnit

@Composable
fun RepeatIntervalDialog(
    initialRepeatInterval: RepeatInterval?,
    onConfirm: (RepeatInterval) -> Unit,
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
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(0.9f)
            ) {
                Text(
                    text = stringResource(R.string.repeats_every_header),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    )
                )

                var repeatAmount by remember {
                    mutableStateOf(initialRepeatInterval?.amount?.toString() ?: "1")
                }

                var repeatUnit by remember {
                    mutableStateOf(initialRepeatInterval?.unit ?: ChronoUnit.DAYS)
                }

                RepeatIntervalDialogContent(
                    repeatAmount = repeatAmount,
                    onRepeatAmountChange = { repeatAmount = it },
                    repeatUnit = repeatUnit,
                    onRepeatUnitChange = { repeatUnit = it }
                )

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                ) {
                    TextButton(
                        onClick = {
                            onDismiss()
                        },
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.dialog_action_cancel),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    TextButton(
                        onClick = {
                            val repeatInterval = RepeatInterval(
                                amount = repeatAmount.toIntOrNull() ?: 1,
                                unit = repeatUnit
                            )
                            onConfirm(repeatInterval)
                            onDismiss()
                        },
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary,
                            disabledContentColor = MaterialTheme.colorScheme.outline
                        ),
                        enabled = repeatAmount.isNotEmpty()
                    ) {
                        Text(
                            text = stringResource(R.string.dialog_action_apply),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RepeatIntervalDialogContent(
    repeatAmount: String,
    onRepeatAmountChange: (String) -> Unit,
    repeatUnit: ChronoUnit,
    onRepeatUnitChange: (ChronoUnit) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.weight(1f)
        ) {
            RepeatAmountInput(
                repeatAmount = repeatAmount,
                onRepeatAmountChange = onRepeatAmountChange
            )
        }

        Spacer(Modifier.width(16.dp))

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .selectableGroup()
                .weight(1f)
        ) {
            RepeatUnitInput(
                repeatUnit = repeatUnit,
                onRepeatUnitChange = onRepeatUnitChange
            )
        }
    }
}

@Composable
private fun RepeatAmountInput(
    repeatAmount: String,
    onRepeatAmountChange: (String) -> Unit
) {
    OutlinedTextField(
        value = repeatAmount,
        onValueChange = { amount ->
            if (amount.length <= 2) {
                val validatedAmount = amount
                    .filter { it.isDigit() }
                    .trimStart { it == '0' }

                onRepeatAmountChange(validatedAmount)
            }
        },
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            textAlign = TextAlign.Center
        ),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Number
        ),
        singleLine = true,
        modifier = Modifier
            .width(72.dp)
            .padding(8.dp)
            .testTag(stringResource(R.string.test_tag_text_field_repeat_amount))
    )
}

@Composable
private fun RepeatUnitInput(
    repeatUnit: ChronoUnit,
    onRepeatUnitChange: (ChronoUnit) -> Unit
) {
    val repeatUnitOptions = listOf(
        ChronoUnit.DAYS,
        ChronoUnit.WEEKS
    )

    repeatUnitOptions.forEach { chronoUnit ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .selectable(
                    selected = (chronoUnit == repeatUnit),
                    onClick = { onRepeatUnitChange(chronoUnit) },
                    role = Role.RadioButton
                )
                .fillMaxWidth(0.8f)
        ) {
            RadioButton(
                selected = (chronoUnit == repeatUnit),
                onClick = null,
                modifier = Modifier
                    .padding(8.dp)
                    .testTag(chronoUnit.name)
            )

            val radioText = when (chronoUnit) {
                ChronoUnit.DAYS -> stringResource(R.string.repeat_unit_option_day)
                else -> stringResource(R.string.repeat_unit_option_week)
            }

            Text(
                text = radioText,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
fun RepeatIntervalDialogPreview() {
    AppTheme {
        RepeatIntervalDialog(
            initialRepeatInterval = null,
            onConfirm = {},
            onDismiss = {}
        )
    }
}
