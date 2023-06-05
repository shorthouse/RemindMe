package dev.shorthouse.remindme.ui.screen.list

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.data.protodatastore.ReminderFilter
import dev.shorthouse.remindme.data.protodatastore.ReminderSort
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.ui.component.dialog.NotificationPermissionRequester
import dev.shorthouse.remindme.ui.component.dialog.ReminderSortDialog
import dev.shorthouse.remindme.ui.component.emptystate.EmptyStateCompletedReminders
import dev.shorthouse.remindme.ui.component.emptystate.EmptyStateOverdueReminders
import dev.shorthouse.remindme.ui.component.emptystate.EmptyStateUpcomingReminders
import dev.shorthouse.remindme.ui.component.progressindicator.CenteredCircularProgressIndicator
import dev.shorthouse.remindme.ui.component.snackbar.RemindMeSnackbarHost
import dev.shorthouse.remindme.ui.component.topappbar.RemindMeTopAppBar
import dev.shorthouse.remindme.ui.previewprovider.ReminderListProvider
import dev.shorthouse.remindme.ui.screen.addedit.add.ReminderAddBottomSheet
import dev.shorthouse.remindme.ui.screen.destinations.ReminderDetailsScreenDestination
import dev.shorthouse.remindme.ui.screen.destinations.ReminderSearchScreenDestination
import dev.shorthouse.remindme.ui.screen.destinations.SettingsScreenDestination
import dev.shorthouse.remindme.ui.theme.AppTheme
import kotlinx.collections.immutable.ImmutableList

@Composable
@RootNavGraph(start = true)
@Destination
fun ReminderListScreen(
    navigator: DestinationsNavigator,
    viewModel: ReminderListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NotificationPermissionRequester()

    ReminderListScreen(
        uiState = uiState,
        onHandleEvent = { viewModel.handleEvent(it) },
        onNavigateSearch = { navigator.navigate(ReminderSearchScreenDestination) },
        onNavigateSettings = { navigator.navigate(SettingsScreenDestination) },
        onNavigateDetails = { navigator.navigate(ReminderDetailsScreenDestination(it.id)) }
    )
}

@Composable
fun ReminderListScreen(
    uiState: ReminderListUiState,
    onHandleEvent: (ReminderListEvent) -> Unit,
    onNavigateSearch: () -> Unit,
    onNavigateSettings: () -> Unit,
    onNavigateDetails: (Reminder) -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            ReminderListTopBar(
                reminderSortOrder = uiState.reminderSortOrder,
                onApplySort = { onHandleEvent(ReminderListEvent.UpdateSortOrder(it)) },
                onNavigateSearch = onNavigateSearch,
                onNavigateSettings = onNavigateSettings
            )
        },
        content = { scaffoldPadding ->
            if (uiState.isLoading) {
                CenteredCircularProgressIndicator()
            } else {
                ReminderListContent(
                    reminders = uiState.reminders,
                    reminderFilter = uiState.reminderFilter,
                    onApplyFilter = { onHandleEvent(ReminderListEvent.UpdateFilter(it)) },
                    onNavigateDetails = onNavigateDetails,
                    onCompleteReminder = { onHandleEvent(ReminderListEvent.CompleteReminder(it)) },
                    modifier = Modifier.padding(scaffoldPadding)
                )

                if (uiState.isAddReminderSheetShown) {
                    ReminderAddBottomSheet(
                        onDismissSheet = { onHandleEvent(ReminderListEvent.HideAddReminderSheet) }
                    )
                }
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = !uiState.isAddReminderSheetShown && !uiState.isLoading,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                FloatingActionButton(
                    onClick = { onHandleEvent(ReminderListEvent.ShowAddReminderSheet) },
                    shape = RoundedCornerShape(16.dp),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.cd_fab_add_reminder),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        },
        snackbarHost = {
            RemindMeSnackbarHost(snackbarHostState = snackbarHostState)
        },
        modifier = modifier
    )

    uiState.snackbarMessage?.let { message ->
        val snackbarText = stringResource(message.messageId)

        LaunchedEffect(snackbarHostState, snackbarText) {
            snackbarHostState.showSnackbar(
                message = snackbarText,
                duration = SnackbarDuration.Short
            )

            onHandleEvent(ReminderListEvent.RemoveSnackbarMessage)
        }
    }
}

@Composable
fun ReminderListTopBar(
    reminderSortOrder: ReminderSort,
    onApplySort: (ReminderSort) -> Unit,
    onNavigateSearch: () -> Unit,
    onNavigateSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSortDialogOpen by remember { mutableStateOf(false) }
    var showOverflowMenu by remember { mutableStateOf(false) }

    if (isSortDialogOpen) {
        ReminderSortDialog(
            initialSort = reminderSortOrder,
            onApplySort = onApplySort,
            onDismiss = { isSortDialogOpen = false }
        )
    }

    RemindMeTopAppBar(
        title = stringResource(R.string.app_name),
        actions = {
            IconButton(onClick = { isSortDialogOpen = true }) {
                Icon(
                    imageVector = Icons.Rounded.SwapVert,
                    contentDescription = stringResource(R.string.cd_sort_reminders)
                )
            }
            IconButton(onClick = onNavigateSearch) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = stringResource(R.string.cd_search_reminders)
                )
            }
            IconButton(onClick = { showOverflowMenu = !showOverflowMenu }) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = stringResource(R.string.cd_more)
                )
            }
            Box {
                DropdownMenu(
                    expanded = showOverflowMenu,
                    onDismissRequest = { showOverflowMenu = false }
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = stringResource(R.string.dropdown_settings),
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 17.sp)
                            )
                        },
                        onClick = {
                            showOverflowMenu = false
                            onNavigateSettings()
                        }
                    )
                }
            }
        },
        modifier = modifier
    )
}

@Composable
private fun ReminderListContent(
    reminders: ImmutableList<Reminder>,
    reminderFilter: ReminderFilter,
    onApplyFilter: (ReminderFilter) -> Unit,
    onNavigateDetails: (Reminder) -> Unit,
    onCompleteReminder: (Reminder) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        ReminderListFilterChips(
            selectedReminderFilter = reminderFilter,
            onApplyFilter = onApplyFilter
        )

        ReminderList(
            reminders = reminders,
            emptyState = {
                when (reminderFilter) {
                    ReminderFilter.UPCOMING -> EmptyStateUpcomingReminders()
                    ReminderFilter.OVERDUE -> EmptyStateOverdueReminders()
                    ReminderFilter.COMPLETED -> EmptyStateCompletedReminders()
                }
            },
            onReminderCard = { onNavigateDetails(it) },
            onCompleteReminder = onCompleteReminder,
            contentPadding = PaddingValues(bottom = 92.dp)
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ReminderListFilterChips(
    selectedReminderFilter: ReminderFilter,
    onApplyFilter: (ReminderFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(top = 6.dp, bottom = 4.dp)
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
                    elevation = 4.dp
                ),
                colors = FilterChipDefaults.elevatedFilterChipColors(
                    labelColor = MaterialTheme.colorScheme.primary,
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReminderList(
    reminders: ImmutableList<Reminder>,
    emptyState: @Composable () -> Unit,
    onReminderCard: (Reminder) -> Unit,
    onCompleteReminder: (Reminder) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    if (reminders.isEmpty()) {
        emptyState()
    } else {
        LazyColumn(
            contentPadding = contentPadding,
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
                .testTag(stringResource(R.string.test_tag_reminder_list_lazy_column))
        ) {
            // Workaround for https://issuetracker.google.com/issues/209652366
            item(key = "0") {
                Spacer(Modifier.padding(1.dp))
            }
            items(
                count = reminders.size,
                key = { reminders[it].id },
                itemContent = { index ->
                    ReminderCard(
                        reminder = reminders[index],
                        onReminderCard = onReminderCard,
                        onCompleteReminder = onCompleteReminder,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .animateItemPlacement(
                                animationSpec = tween(
                                    durationMillis = 400,
                                    easing = LinearOutSlowInEasing
                                )
                            )
                    )
                }
            )
        }
    }
}

@Composable
@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun ReminderListPreview(
    @PreviewParameter(ReminderListProvider::class) reminders: ImmutableList<Reminder>
) {
    AppTheme {
        ReminderListScreen(
            uiState = ReminderListUiState(
                reminders = reminders
            ),
            onHandleEvent = {},
            onNavigateSearch = {},
            onNavigateSettings = {},
            onNavigateDetails = {}
        )
    }
}
