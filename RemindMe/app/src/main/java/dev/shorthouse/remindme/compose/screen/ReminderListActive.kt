package dev.shorthouse.remindme.compose.screen

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.component.dialog.NotificationPermissionRequester
import dev.shorthouse.remindme.compose.component.dialog.ReminderSortDialog
import dev.shorthouse.remindme.compose.component.emptystate.EmptyStateActiveReminders
import dev.shorthouse.remindme.compose.component.list.ReminderListContent
import dev.shorthouse.remindme.compose.component.sheet.BottomSheetReminderActions
import dev.shorthouse.remindme.compose.previewdata.ReminderListProvider
import dev.shorthouse.remindme.compose.screen.destinations.ReminderAddScreenDestination
import dev.shorthouse.remindme.compose.screen.destinations.ReminderEditScreenDestination
import dev.shorthouse.remindme.compose.screen.destinations.ReminderListCompletedScreenDestination
import dev.shorthouse.remindme.compose.screen.destinations.ReminderListSearchScreenDestination
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.data.protodatastore.ReminderSortOrder
import dev.shorthouse.remindme.theme.RemindMeTheme
import dev.shorthouse.remindme.theme.Scrim
import dev.shorthouse.remindme.viewmodel.ListActiveViewModel
import dev.shorthouse.remindme.viewmodel.ListViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalLifecycleComposeApi::class)
@RootNavGraph(start = true)
@Destination
@Composable
fun ReminderListActiveScreen(
    listActiveViewModel: ListActiveViewModel = hiltViewModel(),
    listViewModel: ListViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val uiState by listActiveViewModel.uiState.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()

    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    var selectedReminderState by remember { mutableStateOf(ReminderState()) }

    NotificationPermissionRequester()

    ModalBottomSheetLayout(
        content = {
            ReminderListActiveScaffold(
                activeReminderStates = uiState.activeReminderStates,
                reminderSortOrder = uiState.reminderSortOrder,
                onApplySort = { listActiveViewModel.updateReminderSortOrder(it) },
                onReminderCard = { reminderState ->
                    selectedReminderState = reminderState
                    coroutineScope.launch { bottomSheetState.show() }
                },
                onNavigateAdd = { navigator.navigate(ReminderAddScreenDestination()) },
                onNavigateCompletedReminders = { navigator.navigate(ReminderListCompletedScreenDestination()) },
                onNavigateSearch = { navigator.navigate(ReminderListSearchScreenDestination()) },
                isLoading = uiState.isLoading
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
fun ReminderListActiveScaffold(
    activeReminderStates: List<ReminderState>,
    reminderSortOrder: ReminderSortOrder,
    onNavigateCompletedReminders: () -> Unit,
    onNavigateAdd: () -> Unit,
    onReminderCard: (ReminderState) -> Unit,
    onApplySort: (ReminderSortOrder) -> Unit,
    onNavigateSearch: () -> Unit,
    isLoading: Boolean,
) {
    Scaffold(
        topBar = {
            ReminderListActiveTopBar(
                reminderSortOrder = reminderSortOrder,
                onApplySort = onApplySort,
                onNavigateCompletedReminders = onNavigateCompletedReminders,
                onNavigateSearch = onNavigateSearch
            )
        },
        content = { scaffoldPadding ->
            if (!isLoading) {
                val modifier = Modifier.padding(scaffoldPadding)

                ReminderListContent(
                    reminderStates = activeReminderStates,
                    emptyStateContent = { EmptyStateActiveReminders() },
                    onReminderCard = onReminderCard,
                    contentPadding = PaddingValues(
                        start = dimensionResource(R.dimen.margin_tiny),
                        top = dimensionResource(R.dimen.margin_tiny),
                        end = dimensionResource(R.dimen.margin_tiny),
                        bottom = dimensionResource(R.dimen.margin_bottom_bar)
                    ),
                    modifier = modifier
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateAdd) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.cd_fab_add_reminder)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    )
}

@Composable
fun ReminderListActiveTopBar(
    reminderSortOrder: ReminderSortOrder,
    onApplySort: (ReminderSortOrder) -> Unit,
    onNavigateCompletedReminders: () -> Unit,
    onNavigateSearch: () -> Unit
) {
    var isSortDialogOpen by remember { mutableStateOf(false) }

    if (isSortDialogOpen) {
        ReminderSortDialog(
            initialSort = reminderSortOrder,
            onApplySort = onApplySort,
            onDismiss = { isSortDialogOpen = false }
        )
    }

    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.h6,
            )
        },
        actions = {
            IconButton(onClick = { isSortDialogOpen = true }) {
                Icon(
                    imageVector = Icons.Rounded.SwapVert,
                    contentDescription = stringResource(R.string.cd_sort_reminders),
                    tint = MaterialTheme.colors.onPrimary
                )
            }
            IconButton(onClick = onNavigateCompletedReminders) {
                Icon(
                    imageVector = Icons.Rounded.History,
                    contentDescription = stringResource(R.string.cd_completed_reminders),
                    tint = MaterialTheme.colors.onPrimary
                )
            }
            IconButton(onClick = onNavigateSearch) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = stringResource(R.string.cd_search_reminders),
                    tint = MaterialTheme.colors.onPrimary
                )
            }
        }
    )
}

@Composable
@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
fun ReminderListActivePreview(
    @PreviewParameter(ReminderListProvider::class) reminderStates: List<ReminderState>
) {
    RemindMeTheme {
        ReminderListActiveScaffold(
            activeReminderStates = reminderStates,
            reminderSortOrder = ReminderSortOrder.BY_EARLIEST_DATE_FIRST,
            onNavigateCompletedReminders = {},
            onNavigateSearch = {},
            onReminderCard = {},
            onApplySort = {},
            onNavigateAdd = {},
            isLoading = false
        )
    }
}
