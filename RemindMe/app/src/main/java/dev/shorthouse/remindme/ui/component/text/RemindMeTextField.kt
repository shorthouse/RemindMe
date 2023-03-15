package dev.shorthouse.remindme.ui.component.text

import android.content.res.Configuration
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import dev.shorthouse.remindme.ui.theme.AppTheme

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
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
        keyboardOptions = KeyboardOptions(
            imeAction = imeAction,
            autoCorrect = true
        ),
        decorationBox = { innerTextField ->
            if (text.isEmpty()) {
                Text(
                    text = hintText,
                    style = textStyle,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            innerTextField()
        },
        modifier = modifier
    )
}

@Composable
@Preview(name = "Light Mode", showBackground = true, widthDp = 200)
@Preview(
    name = "Dark Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    widthDp = 200
)
fun RemindMeTextFieldPreview() {
    AppTheme {
        var text by remember { mutableStateOf("") }
        val onTextChange: (String) -> Unit = { text = it }
        val textStyle = MaterialTheme.typography.titleLarge
        val hintText = "Hint text"
        val imeAction = ImeAction.Default

        RemindMeTextField(
            text = text,
            onTextChange = onTextChange,
            textStyle = textStyle,
            hintText = hintText,
            imeAction = imeAction
        )
    }
}
