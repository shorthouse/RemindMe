package dev.shorthouse.remindme.compose.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.state.ReminderState

@Composable
fun ReminderNameInput(reminderState: ReminderState, focusRequester: FocusRequester, modifier: Modifier = Modifier) {
    val nameMaxLength = integerResource(R.integer.reminder_name_max_length)

    ReminderTextField(
        text = reminderState.name,
        onTextChange = { if (it.length <= nameMaxLength) reminderState.name = it },
        textStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
        hintText = stringResource(R.string.hint_reminder_name),
        imeAction = ImeAction.Done,
        modifier = modifier
            .focusRequester(focusRequester)
    )
}

@Composable
fun ReminderDateInput(reminderState: ReminderState, modifier: Modifier = Modifier) {
    val dateDialogState = rememberMaterialDialogState()

    ReminderDatePickerDialog(
        reminderState = reminderState,
        dialogState = dateDialogState
    )

    TextWithLeftIcon(
        icon = painterResource(R.drawable.ic_calendar),
        text = reminderState.date,
        modifier = modifier
            .clickable { dateDialogState.show() }
    )
}

@Composable
fun ReminderTimeInput(reminderState: ReminderState, modifier: Modifier = Modifier) {
    val timeDialogState = rememberMaterialDialogState()

    ReminderTimePickerDialog(
        reminderState = reminderState,
        dialogState = timeDialogState,
    )

    TextWithLeftIcon(
        icon = painterResource(R.drawable.ic_clock),
        text = reminderState.time.toString(),
        modifier = modifier
            .clickable { timeDialogState.show() }
    )
}

@Composable
fun ReminderNotificationInput(reminderState: ReminderState) {
    ReminderSwitchRow(
        icon = painterResource(R.drawable.ic_notification_outline),
        switchText = stringResource(R.string.title_send_notification),
        isChecked = reminderState.isNotificationSent,
        onCheckedChange = { reminderState.isNotificationSent = it }
    )
}

@Composable
fun ReminderNotesInput(reminderState: ReminderState, modifier: Modifier = Modifier) {
    val notesMaxLength = integerResource(R.integer.reminder_notes_max_length)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_notes),
            contentDescription = null,
            tint = colorResource(R.color.icon_grey),
        )

        Spacer(Modifier.width(dimensionResource(R.dimen.margin_normal)))

        ReminderTextField(
            text = reminderState.notes.orEmpty(),
            onTextChange = { if (it.length <= notesMaxLength) reminderState.notes = it },
            textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Normal),
            hintText = stringResource(R.string.hint_reminder_notes),
            imeAction = ImeAction.None,
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ReminderRepeatIntervalInput(reminderState: ReminderState) {
    ReminderSwitchRow(
        icon = painterResource(R.drawable.ic_repeat),
        switchText = stringResource(R.string.title_repeat_reminder),
        isChecked = reminderState.isRepeatReminder,
        onCheckedChange = { reminderState.isRepeatReminder = it }
    )

    val repeatAmount = reminderState.repeatAmount.toIntOrNull() ?: 0
    reminderState.repeatUnit = when {
        stringResource(R.string.day) in reminderState.repeatUnit -> pluralStringResource(
            R.plurals.radio_button_days,
            repeatAmount
        )
        else -> pluralStringResource(
            R.plurals.radio_button_weeks,
            repeatAmount
        )
    }

    if (reminderState.isRepeatReminder) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            RepeatIntervalHeader()

            Spacer(Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RepeatAmountInput(reminderState = reminderState)
                Spacer(Modifier.width(dimensionResource(R.dimen.margin_large)))
                RepeatUnitInput(reminderState = reminderState)
            }
        }
    }
}

@Composable
private fun RepeatIntervalHeader(modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.repeats_every_header),
            color = colorResource(R.color.subtitle_grey)
        )
    }
}

@Composable
private fun RepeatAmountInput(reminderState: ReminderState) {
    val repeatAmountMaxLength = integerResource(R.integer.reminder_repeat_amount_max_length)

    OutlinedTextField(
        value = reminderState.repeatAmount,
        onValueChange = {
            if (it.length <= repeatAmountMaxLength) reminderState.repeatAmount = sanitiseRepeatAmount(it)
        },
        textStyle = TextStyle(textAlign = TextAlign.Center),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Number
        ),
        modifier = Modifier
            .width(72.dp)
            .padding(end = dimensionResource(R.dimen.margin_normal))
    )
}

private fun sanitiseRepeatAmount(repeatAmount: String): String {
    return repeatAmount
        .trimStart { it == '0' }
        .filter { it.isDigit() }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun RepeatUnitInput(reminderState: ReminderState) {
    val repeatUnitPluralIds = listOf(R.plurals.radio_button_days, R.plurals.radio_button_weeks)
    val repeatAmount = reminderState.repeatAmount.toIntOrNull() ?: 0

    val repeatUnitOptions = repeatUnitPluralIds.map { pluralId -> pluralStringResource(pluralId, repeatAmount) }

    Column {
        repeatUnitOptions.forEach { text ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .selectable(
                        selected = (text == reminderState.repeatUnit),
                        onClick = { reminderState.repeatUnit = text }
                    )
            ) {
                RadioButton(
                    selected = (text == reminderState.repeatUnit),
                    onClick = { reminderState.repeatUnit = text }
                )

                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.margin_tiny)))

                Text(text = text)
            }
        }
    }
}
