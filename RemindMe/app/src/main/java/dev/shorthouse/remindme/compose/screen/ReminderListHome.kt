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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.component.BottomSheetNavigate
import dev.shorthouse.remindme.compose.component.BottomSheetSort
import dev.shorthouse.remindme.compose.component.NotificationPermissionRequester
import dev.shorthouse.remindme.compose.screen.destinations.ReminderAddScreenDestination
import dev.shorthouse.remindme.theme.RemindMeTheme
import dev.shorthouse.remindme.utilities.enums.ReminderBottomSheet
import dev.shorthouse.remindme.utilities.enums.ReminderList
import dev.shorthouse.remindme.utilities.enums.ReminderSortOrder
import dev.shorthouse.remindme.viewmodel.ListHomeViewModel
import kotlinx.coroutines.launch

@RootNavGraph(start = true)
@Destination
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ReminderListHomeScreen(
    navigator: DestinationsNavigator
) {
    val listHomeViewModel: ListHomeViewModel = viewModel()

    val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val selectedSheet = remember { mutableStateOf(ReminderBottomSheet.NAVIGATE) }

    val coroutineScope = rememberCoroutineScope()

    val openSheet: (ReminderBottomSheet) -> Unit = { selectedBottomSheet ->
        selectedSheet.value = selectedBottomSheet
        coroutineScope.launch { sheetState.show() }
    }
    val closeSheet: () -> Unit = {
        coroutineScope.launch { sheetState.hide() }
    }

    NotificationPermissionRequester()

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            when (selectedSheet.value) {
                ReminderBottomSheet.NAVIGATE -> BottomSheetNavigate(
                    selectedIndex = listHomeViewModel.selectedNavigateIndex,
                    onSelected = {
                        listHomeViewModel.selectedNavigateIndex = it
                        closeSheet()
                    }
                )
                ReminderBottomSheet.SORT -> BottomSheetSort(
                    selectedIndex = listHomeViewModel.selectedSortIndex,
                    onSelected = {
                        listHomeViewModel.selectedSortIndex = it
                        closeSheet()
                    }
                )
            }
        },
        content = {
            BackHandler(enabled = sheetState.isVisible) {
                closeSheet()
            }

            ReminderListHomeScaffold(
                onNavigationMenu = { openSheet(ReminderBottomSheet.NAVIGATE) },
                onSort = { openSheet(ReminderBottomSheet.SORT) },
                selectedReminderList = listHomeViewModel.selectedReminderList,
                navigator = navigator,
                selectedReminderSortOrder = listHomeViewModel.selectedReminderSortOrder
            )
        }
    )
}

@Composable
fun ReminderListHomeScaffold(
    onNavigationMenu: () -> Unit,
    onSort: () -> Unit,
    selectedReminderList: ReminderList,
    navigator: DestinationsNavigator,
    selectedReminderSortOrder: ReminderSortOrder
) {
    val topBarTitle = when (selectedReminderList) {
        ReminderList.OVERDUE -> stringResource(R.string.overdue_reminders)
        ReminderList.SCHEDULED -> stringResource(R.string.scheduled_reminders)
        ReminderList.COMPLETED -> stringResource(R.string.completed_reminders)
    }

    Scaffold(
        topBar = {
            ReminderListHomeTopBar(
                title = topBarTitle
            )
        },
        bottomBar = {
            ReminderListHomeBottomBar(
                onNavigationMenu = onNavigationMenu,
                onSort = onSort,
            )
        },
        content = { scaffoldPadding ->
            val modifier = Modifier.padding(scaffoldPadding)

            Crossfade(targetState = selectedReminderList) { reminderList ->
                when (reminderList) {
                    ReminderList.OVERDUE -> ReminderListOverdueScreen(
                        navigator = navigator,
                        selectedReminderSortOrder = selectedReminderSortOrder,
                        modifier = modifier
                    )
                    ReminderList.SCHEDULED -> ReminderListScheduledScreen(
                        navigator = navigator,
                        selectedReminderSortOrder = selectedReminderSortOrder,
                        modifier = modifier
                    )
                    ReminderList.COMPLETED -> ReminderListCompletedScreen(
                        navigator = navigator,
                        selectedReminderSortOrder = selectedReminderSortOrder,
                        modifier = modifier
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navigator.navigate(ReminderAddScreenDestination()) }) {
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
fun ReminderListHomeTopBar(
    title: String,
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.h5,
                color = colorResource(R.color.on_primary)
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
                tint = colorResource(R.color.on_primary)
            )
        }

        Spacer(Modifier.weight(1f))

        IconButton(onClick = onSort) {
            Icon(
                imageVector = Icons.Rounded.SwapVert,
                contentDescription = stringResource(R.string.cd_bottom_app_bar_sort),
                tint = colorResource(R.color.on_primary)
            )
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ReminderListHomePreview() {
    RemindMeTheme {
        ReminderListHomeScaffold(
            onNavigationMenu = {},
            onSort = {},
            selectedReminderList = ReminderList.OVERDUE,
            navigator = EmptyDestinationsNavigator,
            selectedReminderSortOrder = ReminderSortOrder.EARLIEST_DATE_FIRST
        )
    }
}
