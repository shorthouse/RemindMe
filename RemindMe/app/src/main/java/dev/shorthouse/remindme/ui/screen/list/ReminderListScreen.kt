package dev.shorthouse.remindme.ui.screen.list

import android.content.res.Configuration
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
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
import dev.shorthouse.remindme.data.protodatastore.ReminderFilter
import dev.shorthouse.remindme.data.protodatastore.ReminderSort
import dev.shorthouse.remindme.ui.component.dialog.NotificationPermissionRequester
import dev.shorthouse.remindme.ui.component.dialog.ReminderSortDialog
import dev.shorthouse.remindme.ui.component.emptystate.EmptyStateActiveReminders
import dev.shorthouse.remindme.ui.component.list.ReminderListContent
import dev.shorthouse.remindme.ui.component.sheet.BottomSheetReminderActions
import dev.shorthouse.remindme.ui.previewdata.ReminderListProvider
import dev.shorthouse.remindme.ui.screen.destinations.ReminderAddScreenDestination
import dev.shorthouse.remindme.ui.screen.destinations.ReminderEditScreenDestination
import dev.shorthouse.remindme.ui.screen.destinations.ReminderListSearchScreenDestination
import dev.shorthouse.remindme.ui.state.ReminderState
import dev.shorthouse.remindme.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@RootNavGraph(start = true)
@Destination
@Composable
fun ReminderListActiveScreen(
    listActiveViewModel: ListActiveViewModel = hiltViewModel(),
    listViewModel: SharedListViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val uiState by listActiveViewModel.uiState.collectAsStateWithLifecycle()
    var isModalBottomSheetShown by remember { mutableStateOf(false) }
    var selectedReminderState by remember { mutableStateOf(ReminderState()) }

    NotificationPermissionRequester()

    ReminderListActiveScaffold(
        activeReminderStates = uiState.reminderStates,
        reminderFilters = uiState.reminderFilters,
        onApplyFilter = { listActiveViewModel.toggleReminderFilter(it) },
        reminderSortOrder = uiState.reminderSortOrder,
        onApplySort = { listActiveViewModel.updateReminderSortOrder(it) },
        onReminderCard = { reminderState ->
            selectedReminderState = reminderState
            isModalBottomSheetShown = true
        },
        onNavigateAdd = { navigator.navigate(ReminderAddScreenDestination()) },
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
    reminderFilters: Set<ReminderFilter>,
    onApplyFilter: (ReminderFilter) -> Unit,
    reminderSortOrder: ReminderSort,
    onApplySort: (ReminderSort) -> Unit,
    onNavigateAdd: () -> Unit,
    onReminderCard: (ReminderState) -> Unit,
    onNavigateSearch: () -> Unit,
    isLoading: Boolean
) {
    Scaffold(
        topBar = {
            ReminderListActiveTopBar(
                reminderSortOrder = reminderSortOrder,
                onApplySort = onApplySort,
                onNavigateSearch = onNavigateSearch
            )
        },
        content = { scaffoldPadding ->
            if (!isLoading) {
                val modifier = Modifier.padding(scaffoldPadding)

                Column(modifier = modifier) {
                    ReminderListFilterChips(
                        reminderFilters,
                        onApplyFilter
                    )

                    ReminderListContent(
                        reminderStates = activeReminderStates,
                        emptyStateContent = { EmptyStateActiveReminders() },
                        onReminderCard = onReminderCard,
                        contentPadding = PaddingValues(
                            start = dimensionResource(R.dimen.margin_tiny),
                            top = dimensionResource(R.dimen.margin_tiny),
                            end = dimensionResource(R.dimen.margin_tiny),
                            bottom = dimensionResource(R.dimen.margin_bottom_bar)
                        )
                    )
                }
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
    reminderSortOrder: ReminderSort,
    onApplySort: (ReminderSort) -> Unit,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderListFilterChips(
    reminderFilters: Set<ReminderFilter>,
    onApplyFilter: (ReminderFilter) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(
                top = dimensionResource(R.dimen.margin_tiny),
                start = dimensionResource(R.dimen.margin_minuscule),
                end = dimensionResource(R.dimen.margin_minuscule)
            )
    ) {
        ReminderFilter.values().forEach { reminderFilter ->
            val selected = reminderFilters.contains(reminderFilter)

            ElevatedFilterChip(
                selected = selected,
                onClick = {
                    if (!isLastSelectedFilter(reminderFilter, reminderFilters)) {
                        onApplyFilter(reminderFilter)
                    }
                },
                label = {
                    Text(text = stringResource(reminderFilter.nameStringId))
                },
                elevation = FilterChipDefaults.elevatedFilterChipElevation(
                    elevation = dimensionResource(R.dimen.margin_minuscule)
                ),
                colors = FilterChipDefaults.elevatedFilterChipColors(
                    labelColor = MaterialTheme.colorScheme.primary,
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.margin_tiny))
            )
        }
    }
}

private fun isLastSelectedFilter(
    reminderFilter: ReminderFilter,
    reminderFilters: Set<ReminderFilter>
): Boolean {
    return reminderFilters.size == 1 && reminderFilters.contains(reminderFilter)
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
            reminderFilters = emptySet(),
            onApplyFilter = {},
            reminderSortOrder = ReminderSort.BY_EARLIEST_DATE_FIRST,
            onApplySort = {},
            onNavigateAdd = {},
            onReminderCard = {},
            onNavigateSearch = {},
            isLoading = false
        )
    }
}
