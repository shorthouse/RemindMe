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
        textStyle = textStyle.copy(
            color = MaterialTheme.colorScheme.onBackground
        ),
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
private fun RemindMeTextFieldEmptyPreview() {
    AppTheme {
        var text by remember { mutableStateOf("") }

        RemindMeTextField(
            text = text,
            onTextChange = { text = it },
            textStyle = MaterialTheme.typography.titleLarge,
            hintText = "Hint text",
            imeAction = ImeAction.Default
        )
    }
}

@Composable
@Preview(name = "Light Mode", showBackground = true, widthDp = 200)
@Preview(
    name = "Dark Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    widthDp = 200
)
private fun RemindMeTextFieldPreview() {
    AppTheme {
        var text by remember { mutableStateOf("Input") }

        RemindMeTextField(
            text = text,
            onTextChange = { text = it },
            textStyle = MaterialTheme.typography.titleLarge,
            hintText = "Hint text",
            imeAction = ImeAction.Default
        )
    }
}
