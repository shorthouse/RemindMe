package dev.shorthouse.remindme.compose.screen

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
import dev.shorthouse.remindme.theme.RemindMeTheme
import dev.shorthouse.remindme.theme.Scrim
import dev.shorthouse.remindme.util.enums.ReminderSortOrderOld
import dev.shorthouse.remindme.viewmodel.ListActiveViewModel
import dev.shorthouse.remindme.viewmodel.ListViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalLifecycleComposeApi::class)
@RootNavGraph(start = true)
@Destination
@Composable
fun ReminderListActiveScreen(navigator: DestinationsNavigator) {
    val listViewModel: ListViewModel = hiltViewModel()
    val listActiveViewModel: ListActiveViewModel = hiltViewModel()

    var selectedReminderState by remember { mutableStateOf(ReminderState()) }

    val coroutineScope = rememberCoroutineScope()

    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    val activeReminderStates: List<ReminderState> by listActiveViewModel
        .activeReminderStates
        .collectAsStateWithLifecycle(initialValue = emptyList())

    NotificationPermissionRequester()

    ModalBottomSheetLayout(
        content = {
            ReminderListActiveScaffold(
                activeReminderStates = activeReminderStates,
                reminderListOrder = listViewModel.reminderListOrder.value,
                onApplySort = { listViewModel.reminderListOrder.value = it },
                onReminderCard = { reminderState ->
                    selectedReminderState = reminderState
                    coroutineScope.launch { bottomSheetState.show() }
                },
                onNavigateAdd = { navigator.navigate(ReminderAddScreenDestination()) },
                onNavigateCompletedReminders = { navigator.navigate(ReminderListCompletedScreenDestination()) },
                onNavigateSearch = { navigator.navigate(ReminderListSearchScreenDestination()) }
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
    reminderListOrder: ReminderSortOrderOld,
    onNavigateCompletedReminders: () -> Unit,
    onNavigateAdd: () -> Unit,
    onReminderCard: (ReminderState) -> Unit,
    onApplySort: (ReminderSortOrderOld) -> Unit,
    onNavigateSearch: () -> Unit
) {
    Scaffold(
        topBar = {
            ReminderListActiveTopBar(
                reminderListOrder = reminderListOrder,
                onApplySort = onApplySort,
                onNavigateCompletedReminders = onNavigateCompletedReminders,
                onNavigateSearch = onNavigateSearch
            )
        },
        content = { scaffoldPadding ->
            val modifier = Modifier.padding(scaffoldPadding)

            ReminderListContent(
                reminderStates = activeReminderStates,
                emptyStateContent = { EmptyStateActiveReminders() },
                onReminderCard = onReminderCard,
                modifier = modifier
            )
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
    reminderListOrder: ReminderSortOrderOld,
    onApplySort: (ReminderSortOrderOld) -> Unit,
    onNavigateCompletedReminders: () -> Unit,
    onNavigateSearch: () -> Unit
) {
    var isSortDialogOpen by remember { mutableStateOf(false) }

    if (isSortDialogOpen) {
        ReminderSortDialog(
            initialSort = reminderListOrder,
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
            reminderListOrder = ReminderSortOrderOld.BY_EARLIEST_DATE_FIRST,
            onNavigateCompletedReminders = {},
            onNavigateSearch = {},
            onReminderCard = {},
            onApplySort = {},
            onNavigateAdd = {}
        )
    }
}
