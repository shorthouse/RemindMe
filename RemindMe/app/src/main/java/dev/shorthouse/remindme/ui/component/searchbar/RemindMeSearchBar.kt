package dev.shorthouse.remindme.ui.component.searchbar

import android.content.res.Configuration
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.ui.previewprovider.SearchQueryProvider
import dev.shorthouse.remindme.ui.theme.AppTheme
import dev.shorthouse.remindme.ui.theme.isLightColors
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
@OptIn(ExperimentalComposeUiApi::class)
fun RemindMeSearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onCloseSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    val searchBarBackgroundColor = if (MaterialTheme.colorScheme.isLightColors()) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surface
    }

    Surface(
        color = searchBarBackgroundColor,
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
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
        delay(400.milliseconds)
        focusRequester.requestFocus()
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun RemindMeSearchBarTextField(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onCloseSearch: () -> Unit,
    innerTextField: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    TextFieldDefaults.DecorationBox(
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
                color = MaterialTheme.colorScheme.outline
            )
        },
        leadingIcon = {
            IconButton(
                onClick = onCloseSearch,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.cd_back),
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(
                    onClick = { onSearchQueryChange("") },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = stringResource(R.string.cd_clear_search),
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }
        },
        container = {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = Color.White,
                content = {},
                modifier = modifier
                    .fillMaxWidth()
                    .padding(6.dp)
            )
        }
    )
}

@Composable
@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
private fun RemindMeSearchBarPreview(
    @PreviewParameter(SearchQueryProvider::class) searchQuery: String
) {
    AppTheme {
        RemindMeSearchBar(
            searchQuery = searchQuery,
            onSearchQueryChange = {},
            onCloseSearch = {}
        )
    }
}
