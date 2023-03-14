package dev.shorthouse.remindme.ui.screen.list.active

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.data.protodatastore.ReminderSortOrder
import dev.shorthouse.remindme.ui.component.dialog.NotificationPermissionRequester
import dev.shorthouse.remindme.ui.component.dialog.ReminderSortDialog
import dev.shorthouse.remindme.ui.component.emptystate.EmptyStateActiveReminders
import dev.shorthouse.remindme.ui.component.list.ReminderListContent
import dev.shorthouse.remindme.ui.component.sheet.BottomSheetReminderActions
import dev.shorthouse.remindme.ui.preview.ReminderListProvider
import dev.shorthouse.remindme.ui.screen.destinations.ReminderAddScreenDestination
import dev.shorthouse.remindme.ui.screen.destinations.ReminderEditScreenDestination
import dev.shorthouse.remindme.ui.screen.destinations.ReminderListCompletedScreenDestination
import dev.shorthouse.remindme.ui.screen.destinations.ReminderListSearchScreenDestination
import dev.shorthouse.remindme.ui.screen.list.ListViewModel
import dev.shorthouse.remindme.ui.state.ReminderState
import dev.shorthouse.remindme.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@RootNavGraph(start = true)
@Destination
@Composable
fun ReminderListActiveScreen(
    listActiveViewModel: ListActiveViewModel = hiltViewModel(),
    listViewModel: ListViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val uiState by listActiveViewModel.uiState.collectAsStateWithLifecycle()
    var isModalBottomSheetShown by remember { mutableStateOf(false) }
    var selectedReminderState by remember { mutableStateOf(ReminderState()) }

    NotificationPermissionRequester()

    ReminderListActiveScaffold(
        activeReminderStates = uiState.activeReminderStates,
        reminderSortOrder = uiState.reminderSortOrder,
        onApplySort = { listActiveViewModel.updateReminderSortOrder(it) },
        onReminderCard = { reminderState ->
            selectedReminderState = reminderState
            isModalBottomSheetShown = true
        },
        onNavigateAdd = { navigator.navigate(ReminderAddScreenDestination()) },
        onNavigateCompletedReminders = {
            navigator.navigate(ReminderListCompletedScreenDestination())
        },
        onNavigateSearch = { navigator.navigate(ReminderListSearchScreenDestination()) },
        isLoading = uiState.isLoading
    )

    if (isModalBottomSheetShown) {
        ModalBottomSheet(
            onDismissRequest = { isModalBottomSheetShown = false },
            dragHandle = null,
            tonalElevation = dimensionResource(R.dimen.margin_none)
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
fun ReminderListActiveScaffold(
    activeReminderStates: List<ReminderState>,
    reminderSortOrder: ReminderSortOrder,
    onNavigateCompletedReminders: () -> Unit,
    onNavigateAdd: () -> Unit,
    onReminderCard: (ReminderState) -> Unit,
    onApplySort: (ReminderSortOrder) -> Unit,
    onNavigateSearch: () -> Unit,
    isLoading: Boolean
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
            FloatingActionButton(
                onClick = onNavigateAdd,
                shape = RoundedCornerShape(dimensionResource(R.dimen.margin_normal)),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.cd_fab_add_reminder),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
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

    val topBarColor = if (isSystemInDarkTheme()) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.primary
    }

    val onTopBarColor = if (isSystemInDarkTheme()) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.onPrimary
    }

    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge.copy(color = onTopBarColor)
            )
        },
        actions = {
            IconButton(onClick = { isSortDialogOpen = true }) {
                Icon(
                    imageVector = Icons.Rounded.SwapVert,
                    contentDescription = stringResource(R.string.cd_sort_reminders),
                    tint = onTopBarColor
                )
            }
            IconButton(onClick = onNavigateCompletedReminders) {
                Icon(
                    imageVector = Icons.Rounded.History,
                    contentDescription = stringResource(R.string.cd_completed_reminders),
                    tint = onTopBarColor
                )
            }
            IconButton(onClick = onNavigateSearch) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = stringResource(R.string.cd_search_reminders),
                    tint = onTopBarColor
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = topBarColor
        )
    )
}

@Composable
@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
fun ReminderListActivePreview(
    @PreviewParameter(ReminderListProvider::class) reminderStates: List<ReminderState>
) {
    AppTheme {
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
