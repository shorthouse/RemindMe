package dev.shorthouse.remindme.ui.component.searchbar

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.ui.theme.Grey

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RemindMeSearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onCloseSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    val topAppBarColor = if (isSystemInDarkTheme()) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.primary
    }

    Surface(
        color = topAppBarColor,
        modifier = modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.search_bar_size))
    ) {
        BasicTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = Color.Black
            ),
            cursorBrush = SolidColor(Color.Black),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search,
                autoCorrect = true
            ),
            keyboardActions = KeyboardActions(
                onSearch = { keyboardController?.hide() }
            ),
            decorationBox = @Composable { innerTextField ->
                RemindMeSearchBarTextField(
                    searchQuery = searchQuery,
                    onSearchQueryChange = onSearchQueryChange,
                    onCloseSearch = onCloseSearch,
                    innerTextField = innerTextField
                )
            },
            modifier = Modifier.focusRequester(focusRequester)
        )
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun RemindMeSearchBarTextField(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onCloseSearch: () -> Unit,
    innerTextField: @Composable () -> Unit
) {
    TextFieldDefaults.TextFieldDecorationBox(
        value = searchQuery,
        innerTextField = innerTextField,
        singleLine = true,
        enabled = true,
        visualTransformation = VisualTransformation.None,
        interactionSource = remember { MutableInteractionSource() },
        placeholder = {
            Text(
                text = stringResource(R.string.top_app_bar_search_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = Grey
            )
        },
        leadingIcon = {
            IconButton(
                onClick = onCloseSearch,
                modifier = Modifier.padding(
                    start = dimensionResource(R.dimen.margin_tiny)
                )
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.cd_top_app_bar_back),
                    tint = Grey
                )
            }
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(
                    onClick = { onSearchQueryChange("") },
                    modifier = Modifier.padding(
                        end = dimensionResource(R.dimen.margin_tiny)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = stringResource(R.string.cd_clear_search),
                        tint = Grey
                    )
                }
            }
        },
        container = {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = LocalContentColor.current,
                content = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.search_bar_text_field_padding))
            )
        }
    )
}
