package dev.shorthouse.remindme.compose.screen

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.component.*
import dev.shorthouse.remindme.compose.screen.destinations.ReminderAddScreenDestination
import dev.shorthouse.remindme.compose.screen.destinations.ReminderEditScreenDestination
import dev.shorthouse.remindme.compose.state.ReminderListSheetsState
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.theme.RemindMeTheme
import dev.shorthouse.remindme.util.enums.ReminderAction
import dev.shorthouse.remindme.util.enums.ReminderBottomSheet
import dev.shorthouse.remindme.util.enums.ReminderList
import dev.shorthouse.remindme.util.enums.ReminderSortOrder
import dev.shorthouse.remindme.viewmodel.ListHomeViewModel
import kotlinx.coroutines.launch

@RootNavGraph(start = true)
@Destination
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ReminderListHomeScreen(navigator: DestinationsNavigator) {
    val listHomeViewModel: ListHomeViewModel = hiltViewModel()

    val reminderListSheetsState by remember { listHomeViewModel.reminderListSheetsState }
    val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()

    val toggleSheet = {
        coroutineScope.launch {
            if (sheetState.isVisible) sheetState.hide() else sheetState.show()
        }
    }

    val openSheet: (ReminderBottomSheet) -> Unit = {
        reminderListSheetsState.selectedSheet = it
        toggleSheet()
    }

    val onNavigateItemSelected: (Int) -> Unit = {
        reminderListSheetsState.selectedReminderListIndex = it
        toggleSheet()
    }

    val onNavigateEdit: (Long) -> Unit = {
        navigator.navigate(
            ReminderEditScreenDestination(
                reminderId = listHomeViewModel.selectedReminderState.id
            )
        )
    }

    val onReminderActionItemSelected: (ReminderAction) -> Unit = { reminderAction ->
        coroutineScope.launch {
            sheetState.hide()
            listHomeViewModel.processReminderAction(reminderAction, onNavigateEdit)
        }
    }

    NotificationPermissionRequester()

    ModalBottomSheetLayout(
        content = {
            ReminderListHomeScaffold(
                reminderListSheetsState = reminderListSheetsState,
                onNavigationMenu = { openSheet(ReminderBottomSheet.NAVIGATE) },
                onNavigateAdd = { navigator.navigate(ReminderAddScreenDestination()) },
                onReminderActions = { reminderState ->
                    listHomeViewModel.selectedReminderState = reminderState
                    openSheet(ReminderBottomSheet.REMINDER_ACTIONS)
                },
            )
        },
        sheetContent = {
            BackHandler(enabled = sheetState.isVisible) {
                coroutineScope.launch { sheetState.hide() }
            }

            BottomSheetContent(
                reminderListSheetsState = reminderListSheetsState,
                selectedReminderState = listHomeViewModel.selectedReminderState,
                onNavigateItemSelected = onNavigateItemSelected,
                onReminderActionItemSelected = onReminderActionItemSelected
            )
        },
        sheetState = sheetState,
        scrimColor = Color.Black.copy(alpha = 0.32f),
    )
}

@Composable
fun ReminderListHomeScaffold(
    reminderListSheetsState: ReminderListSheetsState,
    onNavigationMenu: () -> Unit,
    onNavigateAdd: () -> Unit,
    onReminderActions: (ReminderState) -> Unit,
) {
    Scaffold(
        topBar = {
            ReminderListHomeTopBar(reminderListSheetsState = reminderListSheetsState)
        },
        bottomBar = {
            ReminderListHomeBottomBar(
                onNavigationMenu = onNavigationMenu,
            )
        },
        content = { scaffoldPadding ->
            val modifier = Modifier.padding(scaffoldPadding)

            Crossfade(targetState = reminderListSheetsState.selectedReminderList) { selectedReminderList ->
                ReminderListScreen(
                    selectedReminderList = selectedReminderList,
                    selectedReminderSortOrder = reminderListSheetsState.selectedReminderSortOrder,
                    onReminderActions = onReminderActions,
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
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,
    )
}

@Composable
fun ReminderListHomeTopBar(reminderListSheetsState: ReminderListSheetsState) {
    var isOverflowMenuShown by remember { mutableStateOf(false) }

    var isSortDialogOpen by remember { mutableStateOf(false) }
    if (isSortDialogOpen) {
        ReminderSortDialog(
            initialSort = reminderListSheetsState.selectedReminderSortOrder,
            onApplySort = { selectedSortOrder ->
                reminderListSheetsState.selectedReminderSortOrder = selectedSortOrder
            },
            onDismiss = { isSortDialogOpen = false }
        )
    }

    val topBarTitle = when (reminderListSheetsState.selectedReminderList) {
        ReminderList.SCHEDULED -> stringResource(R.string.scheduled_reminders)
        ReminderList.COMPLETED -> stringResource(R.string.completed_reminders)
    }

    TopAppBar(
        title = {
            Text(
                text = topBarTitle,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.testTag(stringResource(R.string.test_tag_list_home_title, topBarTitle))
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
                        //isSortDialogOpen = true
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

@Composable
fun ReminderListHomeBottomBar(
    onNavigationMenu: () -> Unit,
) {
    BottomAppBar(cutoutShape = CircleShape) {
        IconButton(onClick = onNavigationMenu) {
            Icon(
                imageVector = Icons.Rounded.Menu,
                contentDescription = stringResource(R.string.cd_bottom_app_bar_menu),
                tint = MaterialTheme.colors.onPrimary
            )
        }
    }
}

@Composable
private fun BottomSheetContent(
    reminderListSheetsState: ReminderListSheetsState,
    selectedReminderState: ReminderState,
    onNavigateItemSelected: (Int) -> Unit,
    onReminderActionItemSelected: (ReminderAction) -> Unit
) {
    when (reminderListSheetsState.selectedSheet) {
        ReminderBottomSheet.NAVIGATE -> BottomSheetNavigate(
            selectedItemIndex = reminderListSheetsState.selectedReminderListIndex,
            onItemSelected = onNavigateItemSelected
        )
        ReminderBottomSheet.REMINDER_ACTIONS -> BottomSheetReminderActions(
            reminderState = selectedReminderState,
            onItemSelected = onReminderActionItemSelected
        )
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ReminderListHomePreview() {
    RemindMeTheme {
        val reminderListSheetsState = ReminderListSheetsState(
            selectedSheet = ReminderBottomSheet.NAVIGATE,
            selectedReminderListIndex = 0,
            selectedReminderSortOrder = ReminderSortOrder.EARLIEST_DATE_FIRST
        )
        ReminderListHomeScaffold(
            reminderListSheetsState = reminderListSheetsState,
            onNavigationMenu = {},
            onNavigateAdd = {},
            onReminderActions = {},
        )
    }
}
