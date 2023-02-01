package dev.shorthouse.remindme.compose.screen

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.SwapVert
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
import dev.shorthouse.remindme.compose.component.BottomSheetNavigate
import dev.shorthouse.remindme.compose.component.BottomSheetReminderActions
import dev.shorthouse.remindme.compose.component.BottomSheetSort
import dev.shorthouse.remindme.compose.component.NotificationPermissionRequester
import dev.shorthouse.remindme.compose.screen.destinations.ReminderAddScreenDestination
import dev.shorthouse.remindme.compose.screen.destinations.ReminderEditScreenDestination
import dev.shorthouse.remindme.compose.state.ReminderListSheetsState
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.theme.RemindMeTheme
import dev.shorthouse.remindme.utilities.enums.ReminderAction
import dev.shorthouse.remindme.utilities.enums.ReminderBottomSheet
import dev.shorthouse.remindme.utilities.enums.ReminderList
import dev.shorthouse.remindme.utilities.enums.ReminderSortOrder
import dev.shorthouse.remindme.viewmodel.ListHomeViewModel
import kotlinx.coroutines.launch

@RootNavGraph(start = true)
@Destination
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ReminderListHomeScreen(navigator: DestinationsNavigator) {
    val listHomeViewModel: ListHomeViewModel = hiltViewModel()

    //TODO put this in viewmodel
    val reminderListSheetState by remember {
        mutableStateOf(
            ReminderListSheetsState(
                selectedSheet = ReminderBottomSheet.NAVIGATE,
                selectedReminderListIndex = 0,
                selectedReminderSortOrderIndex = 0
            )
        )
    }

    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    val openSheet: (ReminderBottomSheet) -> Unit = { selectedBottomSheet ->
        reminderListSheetState.selectedSheet = selectedBottomSheet
        coroutineScope.launch { sheetState.show() }
    }

    val onNavigateItemSelected: (Int) -> Unit = {
        reminderListSheetState.selectedReminderListIndex = it
        coroutineScope.launch { sheetState.hide() }
    }

    val onSortItemSelected: (Int) -> Unit = {
        reminderListSheetState.selectedReminderSortOrderIndex = it
        coroutineScope.launch { sheetState.hide() }
    }

    val onReminderActionItemSelected: (ReminderAction) -> Unit = { reminderAction ->
        coroutineScope.launch {
            sheetState.hide()

            when (reminderAction) {
                ReminderAction.EDIT -> {
                    navigator.navigate(
                        ReminderEditScreenDestination(reminderId = listHomeViewModel.selectedReminderState.id)
                    )
                }
                ReminderAction.COMPLETE_ONETIME -> {
                    listHomeViewModel.completeOnetimeReminder(listHomeViewModel.selectedReminderState)
                }
                ReminderAction.COMPLETE_REPEAT_OCCURRENCE -> {
                    listHomeViewModel.completeRepeatReminderOccurrence(listHomeViewModel.selectedReminderState)
                }
                ReminderAction.COMPLETE_REPEAT_SERIES -> {
                    listHomeViewModel.completeRepeatReminderSeries(listHomeViewModel.selectedReminderState)
                }
                ReminderAction.DELETE -> {
                    listHomeViewModel.deleteReminder(listHomeViewModel.selectedReminderState)
                }
            }
        }
    }

    NotificationPermissionRequester()

    ModalBottomSheetLayout(
        content = {
            ReminderListHomeScaffold(
                selectedReminderList = reminderListSheetState.selectedReminderList,
                selectedReminderSortOrder = reminderListSheetState.selectedReminderSortOrder,
                onNavigationMenu = { openSheet(ReminderBottomSheet.NAVIGATE) },
                onSort = { openSheet(ReminderBottomSheet.SORT) },
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
                reminderListSheetsState = reminderListSheetState,
                selectedReminderState = listHomeViewModel.selectedReminderState,
                onNavigateItemSelected = onNavigateItemSelected,
                onSortItemSelected = onSortItemSelected,
                onReminderActionItemSelected = onReminderActionItemSelected
            )
        },
        sheetState = sheetState,
        scrimColor = Color.Black.copy(alpha = 0.32f),
    )
}

@Composable
fun ReminderListHomeScaffold(
    selectedReminderList: ReminderList,
    selectedReminderSortOrder: ReminderSortOrder,
    onNavigationMenu: () -> Unit,
    onSort: () -> Unit,
    onNavigateAdd: () -> Unit,
    onReminderActions: (ReminderState) -> Unit,
) {
    Scaffold(
        topBar = {
            ReminderListHomeTopBar(selectedReminderList = selectedReminderList)
        },
        bottomBar = {
            ReminderListHomeBottomBar(
                onNavigationMenu = onNavigationMenu,
                onSort = onSort
            )
        },
        content = { scaffoldPadding ->
            val modifier = Modifier.padding(scaffoldPadding)

            Crossfade(targetState = selectedReminderList) { selectedReminderList ->
                ReminderListScreen(
                    selectedReminderList = selectedReminderList,
                    selectedReminderSortOrder = selectedReminderSortOrder,
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
fun ReminderListHomeTopBar(selectedReminderList: ReminderList) {
    val topBarTitle = when (selectedReminderList) {
        ReminderList.OVERDUE -> stringResource(R.string.overdue_reminders)
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
        }
    )
}

@Composable
fun ReminderListHomeBottomBar(
    onNavigationMenu: () -> Unit,
    onSort: () -> Unit,
) {
    BottomAppBar(cutoutShape = CircleShape) {
        IconButton(onClick = onNavigationMenu) {
            Icon(
                imageVector = Icons.Rounded.Menu,
                contentDescription = stringResource(R.string.cd_bottom_app_bar_menu),
                tint = MaterialTheme.colors.onPrimary
            )
        }

        Spacer(Modifier.weight(1f))

        IconButton(onClick = onSort) {
            Icon(
                imageVector = Icons.Rounded.SwapVert,
                contentDescription = stringResource(R.string.cd_bottom_app_bar_sort),
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
    onSortItemSelected: (Int) -> Unit,
    onReminderActionItemSelected: (ReminderAction) -> Unit
) {
    when (reminderListSheetsState.selectedSheet) {
        ReminderBottomSheet.NAVIGATE -> BottomSheetNavigate(
            selectedItemIndex = reminderListSheetsState.selectedReminderListIndex,
            onItemSelected = onNavigateItemSelected
        )
        ReminderBottomSheet.SORT -> BottomSheetSort(
            selectedItemIndex = reminderListSheetsState.selectedReminderSortOrderIndex,
            onItemSelected = onSortItemSelected
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
        ReminderListHomeScaffold(
            selectedReminderList = ReminderList.OVERDUE,
            selectedReminderSortOrder = ReminderSortOrder.EARLIEST_DATE_FIRST,
            onNavigationMenu = {},
            onSort = {},
            onReminderActions = {},
            onNavigateAdd = {},
        )
    }
}
