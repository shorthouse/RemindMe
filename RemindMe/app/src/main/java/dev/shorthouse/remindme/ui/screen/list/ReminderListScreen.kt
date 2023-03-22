package dev.shorthouse.remindme.ui.screen.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
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
import dev.shorthouse.remindme.ui.component.list.ReminderListContent
import dev.shorthouse.remindme.ui.component.search.RemindMeSearchBar
import dev.shorthouse.remindme.ui.component.sheet.BottomSheetReminderActions
import dev.shorthouse.remindme.ui.screen.destinations.ReminderAddScreenDestination
import dev.shorthouse.remindme.ui.screen.destinations.ReminderEditScreenDestination
import dev.shorthouse.remindme.ui.state.ReminderState
import dev.shorthouse.remindme.ui.util.enums.ReminderAction

@RootNavGraph(start = true)
@Destination
@Composable
fun ReminderListScreen(
    listViewModel: ListViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val uiState by listViewModel.uiState.collectAsStateWithLifecycle()

    NotificationPermissionRequester()

    ReminderListScaffold(
        uiState = uiState,
        onApplyFilter = { listViewModel.updateReminderFilter(it) },
        onApplySort = { listViewModel.updateReminderSortOrder(it) },
        onNavigateAdd = { navigator.navigate(ReminderAddScreenDestination()) },
        onSearch = { listViewModel.updateIsSearchBarShown(true) },
        onSearchQueryChange = { listViewModel.updateSearchQuery(it) },
        onCloseSearch = {
            listViewModel.updateIsSearchBarShown(false)
            listViewModel.updateSearchQuery("")
        },
        onDismissBottomSheet = { listViewModel.updateIsBottomSheetShown(false) },
        onReminderCard = { reminderState ->
            listViewModel.updateBottomSheetReminderState(reminderState)
            listViewModel.updateIsBottomSheetShown(true)
        },
        onReminderActionSelected = { reminderAction ->
            listViewModel.updateIsBottomSheetShown(false)

            when (reminderAction) {
                ReminderAction.EDIT -> navigator.navigate(
                    ReminderEditScreenDestination(
                        reminderId = uiState.bottomSheetReminderState.id
                    )
                )
                else -> listViewModel.processReminderAction(
                    reminderAction = reminderAction,
                    reminderState = uiState.bottomSheetReminderState.copy()
                )
            }
        }
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ReminderListScaffold(
    uiState: ListUiState,
    onApplyFilter: (ReminderFilter) -> Unit,
    onApplySort: (ReminderSort) -> Unit,
    onSearch: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onCloseSearch: () -> Unit,
    onNavigateAdd: () -> Unit,
    onDismissBottomSheet: () -> Unit,
    onReminderCard: (ReminderState) -> Unit,
    onReminderActionSelected: (ReminderAction) -> Unit
) {
    Scaffold(
        topBar = {
            ReminderListTopBar(
                reminderSortOrder = uiState.reminderSortOrder,
                onApplySort = onApplySort,
                onSearch = onSearch,
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                onCloseSearch = onCloseSearch,
                isSearchBarShown = uiState.isSearchBarShown
            )
        },
        content = { scaffoldPadding ->
            if (!uiState.isLoading) {
                Column(modifier = Modifier.padding(scaffoldPadding)) {
                    ReminderListFilterChips(
                        uiState.reminderFilter,
                        onApplyFilter
                    )

                    ReminderListContent(
                        reminderStates = uiState.reminderStates,
                        reminderFilter = uiState.reminderFilter,
                        isSearchBarShown = uiState.isSearchBarShown,
                        isSearchQueryEmpty = uiState.searchQuery.isEmpty(),
                        onReminderCard = onReminderCard,
                        contentPadding = PaddingValues(
                            start = dimensionResource(R.dimen.margin_tiny),
                            top = dimensionResource(R.dimen.margin_tiny),
                            end = dimensionResource(R.dimen.margin_tiny),
                            bottom = dimensionResource(R.dimen.margin_bottom_bar)
                        )
                    )
                }

                if (uiState.isBottomSheetShown) {
                    BottomSheetReminderActions(
                        reminderState = uiState.bottomSheetReminderState,
                        onReminderActionSelected = onReminderActionSelected,
                        onDismissRequest = onDismissBottomSheet
                    )
                }
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = !uiState.isSearchBarShown,
                enter = scaleIn(),
                exit = ExitTransition.None
            ) {
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
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderListTopBar(
    reminderSortOrder: ReminderSort,
    onApplySort: (ReminderSort) -> Unit,
    onSearch: () -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onCloseSearch: () -> Unit,
    isSearchBarShown: Boolean
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

    if (isSearchBarShown) {
        RemindMeSearchBar(
            searchQuery = searchQuery,
            onSearchQueryChange = onSearchQueryChange,
            onCloseSearch = onCloseSearch
        )
    } else {
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
                IconButton(onClick = onSearch) {
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderListFilterChips(
    selectedReminderFilter: ReminderFilter,
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
            ElevatedFilterChip(
                selected = reminderFilter == selectedReminderFilter,
                onClick = {
                    if (reminderFilter != selectedReminderFilter) {
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
                modifier = Modifier.padding(
                    horizontal = dimensionResource(R.dimen.margin_tiny)
                )
            )
        }
    }
}

// @Composable
// @Preview(name = "Light Mode")
// @Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
// fun ReminderListPreview(
//    @PreviewParameter(ReminderListProvider::class) reminderStates: List<ReminderState>
// ) {
//    AppTheme {
//        ReminderListScaffold(
//            uiState = ListUiState(reminderStates = reminderStates),
//            onApplyFilter = {},
//            onApplySort = {},
//            onNavigateAdd = {},
//            onNavigateSearch = {},
//            onReminderCard = {},
//            onDismissBottomSheet = {},
//            onReminderActionSelected = {}
//        )
//    }
// }
