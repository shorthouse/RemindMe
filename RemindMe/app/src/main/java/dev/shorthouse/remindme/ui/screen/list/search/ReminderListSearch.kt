package dev.shorthouse.remindme.ui.screen.list.search

import android.content.res.Configuration
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.ui.component.emptystate.EmptyStateSearchReminders
import dev.shorthouse.remindme.ui.component.list.ReminderListContent
import dev.shorthouse.remindme.ui.component.sheet.BottomSheetReminderActions
import dev.shorthouse.remindme.ui.preview.ReminderListProvider
import dev.shorthouse.remindme.ui.screen.destinations.ReminderEditScreenDestination
import dev.shorthouse.remindme.ui.screen.list.ListViewModel
import dev.shorthouse.remindme.ui.state.ReminderState
import dev.shorthouse.remindme.ui.theme.AppTheme

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Destination
@Composable
fun ReminderListSearchScreen(
    listSearchViewModel: ListSearchViewModel = hiltViewModel(),
    listViewModel: ListViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val uiState by listSearchViewModel.uiState.collectAsStateWithLifecycle()
    var isModalBottomSheetShown by remember { mutableStateOf(false) }
    var selectedReminderState by remember { mutableStateOf(ReminderState()) }
    val keyboardController = LocalSoftwareKeyboardController.current

    ReminderListSearchScaffold(
        searchReminderStates = uiState.searchReminderStates,
        searchQuery = uiState.searchQuery,
        onNavigateUp = { navigator.navigateUp() },
        onSearchQueryChange = { listSearchViewModel.setSearchQuery(it) },
        onClearSearchQuery = { listSearchViewModel.setSearchQuery("") },
        onReminderCard = { reminderState ->
            keyboardController?.hide()
            selectedReminderState = reminderState
            isModalBottomSheetShown = true
        },
        isLoading = uiState.isLoading
    )

    if (isModalBottomSheetShown) {
        ModalBottomSheet(
            onDismissRequest = { isModalBottomSheetShown = false },
            dragHandle = null
        ) {
            BottomSheetReminderActions(
                reminderState = selectedReminderState,
                onReminderActionItemSelected = { reminderAction ->
                    isModalBottomSheetShown = false

                    listViewModel.processReminderAction(
                        selectedReminderState = selectedReminderState.copy(),
                        reminderAction = reminderAction,
                        onEdit = {
                            navigator.navigate(
                                ReminderEditScreenDestination(
                                    reminderId = selectedReminderState.id
                                )
                            )
                        }
                    )
                }
            )
        }
    }
}

@Composable
fun ReminderListSearchScaffold(
    searchReminderStates: List<ReminderState>,
    onNavigateUp: () -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearSearchQuery: () -> Unit,
    onReminderCard: (ReminderState) -> Unit,
    isLoading: Boolean
) {
    Scaffold(
        topBar = {
            ReminderListSearchTopBar(
                onNavigateUp = onNavigateUp,
                searchQuery = searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                onClearSearchQuery = onClearSearchQuery
            )
        },
        content = { scaffoldPadding ->
            if (!isLoading) {
                val modifier = Modifier.padding(scaffoldPadding)

                ReminderListContent(
                    reminderStates = searchReminderStates,
                    emptyStateContent = { EmptyStateSearchReminders() },
                    onReminderCard = onReminderCard,
                    contentPadding = PaddingValues(dimensionResource(R.dimen.margin_tiny)),
                    modifier = modifier
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ReminderListSearchTopBar(
    onNavigateUp: () -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearSearchQuery: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.cd_top_app_bar_back),
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BasicTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
                    cursorBrush = SolidColor(Color.Black),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search,
                        autoCorrect = true
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = { keyboardController?.hide() }
                    ),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = stringResource(R.string.top_app_bar_search_hint),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                        innerTextField()
                    },
                    modifier = modifier
                        .focusRequester(focusRequester)
                        .weight(1f)
                )

                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = onClearSearchQuery) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = stringResource(id = R.string.cd_clear_search),
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun ReminderListSearchPreview(
    @PreviewParameter(ReminderListProvider::class) reminderStates: List<ReminderState>
) {
    AppTheme {
        ReminderListSearchScaffold(
            searchReminderStates = reminderStates,
            onNavigateUp = {},
            searchQuery = "",
            onSearchQueryChange = {},
            onClearSearchQuery = {},
            onReminderCard = {},
            isLoading = false
        )
    }
}
