package dev.shorthouse.remindme.compose.screen

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.component.*
import dev.shorthouse.remindme.compose.component.dialog.ReminderSortDialog
import dev.shorthouse.remindme.compose.component.sheet.BottomSheetReminderActions
import dev.shorthouse.remindme.compose.screen.destinations.ReminderAddScreenDestination
import dev.shorthouse.remindme.compose.screen.destinations.ReminderEditScreenDestination
import dev.shorthouse.remindme.compose.screen.destinations.ReminderListCompletedScreenDestination
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.theme.RemindMeTheme
import dev.shorthouse.remindme.util.enums.ReminderAction
import dev.shorthouse.remindme.util.enums.ReminderSortOrder
import dev.shorthouse.remindme.viewmodel.ListActiveViewModel
import kotlinx.coroutines.launch

@RootNavGraph(start = true)
@Destination
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ReminderListHomeScreen(navigator: DestinationsNavigator) {
    val listViewModel: ListActiveViewModel = hiltViewModel()

    var reminderListSortOrder by remember { mutableStateOf(ReminderSortOrder.EARLIEST_DATE_FIRST) }

    val activeReminderStates = listViewModel.getActiveReminderStates(reminderListSortOrder).observeAsState()

    val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()

    val openSheet: () -> Unit = {
        coroutineScope.launch { sheetState.show() }
    }

    val closeSheet: () -> Unit = {
        coroutineScope.launch { sheetState.hide() }
    }

    val onNavigateEdit: (Long) -> Unit = {
        navigator.navigate(
            ReminderEditScreenDestination(
                reminderId = listViewModel.selectedReminderState.id
            )
        )
    }

    val onReminderActionItemSelected: (ReminderAction) -> Unit = { reminderAction ->
        coroutineScope.launch {
            closeSheet()
            listViewModel.processReminderAction(reminderAction, onNavigateEdit)
        }
    }

    val onApplySort: (ReminderSortOrder) -> Unit = { selectedSortOrder ->
        reminderListSortOrder = selectedSortOrder
    }

    val onNavigateCompletedReminders = { navigator.navigate(ReminderListCompletedScreenDestination()) }

    NotificationPermissionRequester()

    activeReminderStates.value?.let {
        ModalBottomSheetLayout(
            content = {
                ReminderListHomeScaffold(
                    reminderStates = it,
                    reminderListSortOrder = reminderListSortOrder,
                    onNavigateAdd = { navigator.navigate(ReminderAddScreenDestination()) },
                    onReminderCard = { reminderState ->
                        listViewModel.selectedReminderState = reminderState
                        openSheet()
                    },
                    onNavigateCompletedReminders = onNavigateCompletedReminders,
                    onApplySort = onApplySort
                )
            },
            sheetContent = {
                BackHandler(enabled = sheetState.isVisible) {
                    closeSheet()
                }

                BottomSheetReminderActions(
                    reminderState = listViewModel.selectedReminderState,
                    onItemSelected = onReminderActionItemSelected
                )
            },
            sheetState = sheetState,
            scrimColor = Color.Black.copy(alpha = 0.32f),
        )
    }
}

@Composable
fun ReminderListHomeScaffold(
    reminderStates: List<ReminderState>,
    reminderListSortOrder: ReminderSortOrder,
    onNavigateCompletedReminders: () -> Unit,
    onNavigateAdd: () -> Unit,
    onReminderCard: (ReminderState) -> Unit,
    onApplySort: (ReminderSortOrder) -> Unit,
) {
    Scaffold(
        topBar = {
            ReminderListHomeTopBar(
                reminderListSortOrder = reminderListSortOrder,
                onApplySort = onApplySort,
                onNavigateCompletedReminders = onNavigateCompletedReminders
            )
        },
        content = { scaffoldPadding ->
            val modifier = Modifier.padding(scaffoldPadding)

            ReminderList(
                reminderStates = reminderStates,
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
fun ReminderListHomeTopBar(
    reminderListSortOrder: ReminderSortOrder,
    onApplySort: (ReminderSortOrder) -> Unit,
    onNavigateCompletedReminders: () -> Unit
) {
    var isOverflowMenuShown by remember { mutableStateOf(false) }

    var isSortDialogOpen by remember { mutableStateOf(false) }

    if (isSortDialogOpen) {
        ReminderSortDialog(
            initialSort = reminderListSortOrder,
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
            IconButton(onClick = { isOverflowMenuShown = !isOverflowMenuShown }) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = stringResource(R.string.cd_more),
                    tint = MaterialTheme.colors.onPrimary
                )
            }
            DropdownMenu(
                expanded = isOverflowMenuShown,
                onDismissRequest = { isOverflowMenuShown = false },
            ) {
                DropdownMenuItem(
                    onClick = {
                        isOverflowMenuShown = false
                        isSortDialogOpen = true
                    }) {
                    Text(
                        text = stringResource(R.string.dropdown_menu_sort),
                        color = MaterialTheme.colors.onSurface
                    )
                }
                DropdownMenuItem(
                    onClick = {
                        isOverflowMenuShown = false
                        onNavigateCompletedReminders()
                    }) {
                    Text(
                        text = stringResource(R.string.dropdown_menu_completed),
                        color = MaterialTheme.colors.onSurface
                    )
                }
            }
        }
    )
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ReminderListHomePreview() {
    RemindMeTheme {
        ReminderListHomeScaffold(
            onNavigateCompletedReminders = {},
            onNavigateAdd = {},
            onReminderCard = {},
            reminderListSortOrder = ReminderSortOrder.EARLIEST_DATE_FIRST,
            onApplySort = {},
            reminderStates = emptyList()
        )
    }
}
