package dev.shorthouse.remindme.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp
import dev.shorthouse.remindme.R

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
