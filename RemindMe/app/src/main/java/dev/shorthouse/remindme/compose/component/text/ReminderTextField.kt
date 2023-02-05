package dev.shorthouse.remindme.compose.component.text

import android.content.res.Configuration
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import dev.shorthouse.remindme.theme.RemindMeTheme
import dev.shorthouse.remindme.theme.SubtitleGrey

@Composable
fun ReminderTextField(
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
        textStyle = textStyle.copy(color = MaterialTheme.colors.onBackground),
        cursorBrush = SolidColor(MaterialTheme.colors.onBackground),
        keyboardOptions = KeyboardOptions(
            imeAction = imeAction,
            autoCorrect = true
        ),
        decorationBox = { innerTextField ->
            if (text.isEmpty()) {
                Text(
                    text = hintText,
                    style = textStyle,
                    color = SubtitleGrey
                )
            }
            innerTextField()
        },
        modifier = modifier
    )
}

@Preview(name = "Light Mode", showBackground = true, widthDp = 200)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true, widthDp = 200)
@Composable
fun ReminderTextFieldPreview() {
    RemindMeTheme {
        var text by remember { mutableStateOf("") }
        val onTextChange: (String) -> Unit = { text = it }

        ReminderTextField(
            text = text,
            onTextChange = onTextChange,
            textStyle = MaterialTheme.typography.h6,
            hintText = "Hint text",
            imeAction = ImeAction.Default,
        )
    }
}
