package dev.shorthouse.remindme.ui.screen.list.search

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.*
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
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
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
import dev.shorthouse.remindme.ui.theme.RemindMeTheme
import dev.shorthouse.remindme.ui.theme.Scrim
import dev.shorthouse.remindme.ui.theme.SubtitleGrey
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class, ExperimentalLifecycleComposeApi::class)
@Destination
@Composable
fun ReminderListSearchScreen(
    listSearchViewModel: ListSearchViewModel = hiltViewModel(),
    listViewModel: ListViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    listSearchViewModel.initialiseUiState()
    val uiState by listSearchViewModel.uiState.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()

    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    var selectedReminderState by remember { mutableStateOf(ReminderState()) }

    val keyboardController = LocalSoftwareKeyboardController.current

    ModalBottomSheetLayout(
        content = {
            ReminderListSearchScaffold(
                searchReminderStates = uiState.searchReminderStates,
                searchQuery = uiState.searchQuery,
                onNavigateUp = { navigator.navigateUp() },
                onSearchQueryChange = { listSearchViewModel.setSearchQuery(it) },
                onClearSearchQuery = { listSearchViewModel.setSearchQuery("") },
                onReminderCard = { reminderState ->
                    coroutineScope.launch {
                        keyboardController?.hide()
                        selectedReminderState = reminderState
                        bottomSheetState.show()
                    }
                },
            )
        },
        sheetContent = {
            BackHandler(enabled = bottomSheetState.isVisible) {
                coroutineScope.launch { bottomSheetState.hide() }
            }

            BottomSheetReminderActions(
                reminderState = selectedReminderState,
                onReminderActionItemSelected = { reminderAction ->
                    coroutineScope.launch {
                        bottomSheetState.hide()

                        listViewModel.processReminderAction(
                            selectedReminderState = selectedReminderState.copy(),
                            reminderAction = reminderAction,
                            onEdit = {
                                navigator.navigate(ReminderEditScreenDestination(reminderId = selectedReminderState.id))
                            }
                        )
                    }
                }
            )
        },
        sheetState = bottomSheetState,
        scrimColor = Scrim,
    )
}

@Composable
fun ReminderListSearchScaffold(
    searchReminderStates: List<ReminderState>,
    onNavigateUp: () -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearSearchQuery: () -> Unit,
    onReminderCard: (ReminderState) -> Unit,
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
            val modifier = Modifier.padding(scaffoldPadding)

            ReminderListContent(
                reminderStates = searchReminderStates,
                emptyStateContent = { EmptyStateSearchReminders() },
                onReminderCard = onReminderCard,
                contentPadding = PaddingValues(dimensionResource(R.dimen.margin_tiny)),
                modifier = modifier
            )
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ReminderListSearchTopBar(
    onNavigateUp: () -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearSearchQuery: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        content = {
            Surface(
                color = MaterialTheme.colors.onPrimary,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = dimensionResource(R.dimen.search_text_field_padding_horizontal),
                        vertical = dimensionResource(R.dimen.search_text_field_padding_vertical),
                    )
            ) {
                val keyboardController = LocalSoftwareKeyboardController.current
                val focusRequester = remember { FocusRequester() }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.cd_top_app_bar_back),
                            tint = SubtitleGrey
                        )
                    }
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        textStyle = MaterialTheme.typography.body1.copy(color = Color.Black),
                        cursorBrush = SolidColor(Color.Black),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Search,
                            autoCorrect = true
                        ),
                        keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.top_app_bar_search_hint),
                                    style = MaterialTheme.typography.body1,
                                    color = SubtitleGrey
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
                                tint = SubtitleGrey
                            )
                        }
                    }
                }

                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
            }
        }
    )
}

@Composable
@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun ReminderListSearchPreview(
    @PreviewParameter(ReminderListProvider::class) reminderStates: List<ReminderState>
) {
    RemindMeTheme {
        ReminderListSearchScaffold(
            searchReminderStates = reminderStates,
            onNavigateUp = {},
            searchQuery = "",
            onSearchQueryChange = {},
            onClearSearchQuery = {},
            onReminderCard = {},
        )
    }
}
