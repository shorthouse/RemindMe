package dev.shorthouse.remindme.ui.component.text

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
import dev.shorthouse.remindme.ui.theme.RemindMeTheme
import dev.shorthouse.remindme.ui.theme.SubtitleGrey

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

@Composable
@Preview(name = "Light Mode", showBackground = true, widthDp = 200)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true, widthDp = 200)
fun RemindMeTextFieldPreview() {
    RemindMeTheme {
        var text by remember { mutableStateOf("") }
        val onTextChange: (String) -> Unit = { text = it }
        val textStyle = MaterialTheme.typography.h6
        val hintText = "Hint text"
        val imeAction = ImeAction.Default

        RemindMeTextField(
            text = text,
            onTextChange = onTextChange,
            textStyle = textStyle,
            hintText = hintText,
            imeAction = imeAction,
        )
    }
}
