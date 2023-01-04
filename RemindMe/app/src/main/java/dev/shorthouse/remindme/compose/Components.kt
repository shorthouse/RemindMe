package dev.shorthouse.remindme.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.TimePickerDefaults
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.state.ReminderState
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun RemindMeTextField(
    text: String,
    onTextChange: (String) -> Unit,
    textStyle: TextStyle,
    hintText: String,
    imeAction: ImeAction,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = text,
        onValueChange = onTextChange,
        textStyle = textStyle,
        keyboardOptions = KeyboardOptions(
            imeAction = imeAction,
            autoCorrect = true
        ),
        decorationBox = { innerTextField ->
            if (text.isEmpty()) {
                Text(
                    text = hintText,
                    fontSize = textStyle.fontSize,
                    fontWeight = textStyle.fontWeight,
                    color = colorResource(R.color.subtitle_grey)
                )
            }
            innerTextField()
        },
        modifier = modifier
    )
}

@Composable
fun TextWithLeftIcon(
    icon: Painter,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = colorResource(R.color.icon_grey),
        )

        Spacer(Modifier.width(dimensionResource(R.dimen.margin_normal)))

        Text(
            text = text,
            fontSize = 18.sp,
        )
    }
}

@Composable
fun DatePicker(
    dialogState: MaterialDialogState,
    reminderState: ReminderState
) {
    val dialogButtonTextStyle = TextStyle(color = colorResource(R.color.on_surface), fontWeight = FontWeight.Bold)

    MaterialDialog(
        dialogState = dialogState,
        buttons = {
            positiveButton(
                text = stringResource(R.string.dialog_ok),
                textStyle = dialogButtonTextStyle
            )
            negativeButton(
                text = stringResource(R.string.dialog_cancel),
                textStyle = dialogButtonTextStyle
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

@Composable
fun TimePicker(
    dialogState: MaterialDialogState,
    reminderState: ReminderState
) {
    val dialogButtonTextStyle = TextStyle(color = colorResource(R.color.on_surface), fontWeight = FontWeight.Bold)

    MaterialDialog(
        dialogState = dialogState,
        buttons = {
            positiveButton(
                text = stringResource(R.string.dialog_ok),
                textStyle = dialogButtonTextStyle
            )
            negativeButton(
                text = stringResource(R.string.dialog_cancel),
                textStyle = dialogButtonTextStyle
            )
        },
    ) {
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_normal)))

        timepicker(
            initialTime = reminderState.time,
            title = "",
            is24HourClock = true,
            onTimeChange = { reminderState.time = it },
            colors = TimePickerDefaults.colors(
                activeBackgroundColor = colorResource(R.color.blue),
                inactiveBackgroundColor = Color.LightGray,
                inactiveTextColor = Color.Black,
                selectorColor = colorResource(R.color.blue),
            ),
        )
    }
}
