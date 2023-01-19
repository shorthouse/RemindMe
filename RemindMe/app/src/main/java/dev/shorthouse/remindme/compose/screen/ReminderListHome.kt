package dev.shorthouse.remindme.compose.screen

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.material.composethemeadapter.MdcTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.component.BottomSheetNavigate
import dev.shorthouse.remindme.compose.component.BottomSheetSort
import dev.shorthouse.remindme.compose.component.NotificationPermissionRequest
import dev.shorthouse.remindme.compose.screen.destinations.ReminderAddScreenDestination
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
    viewModel: ListHomeViewModel = viewModel(),
    navigator: DestinationsNavigator
) {
    val sheet = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val selectedSheet = remember { mutableStateOf(ReminderBottomSheet.NAVIGATE) }

    val coroutineScope = rememberCoroutineScope()

    val openSheet: (ReminderBottomSheet) -> Unit = { selectedBottomSheet ->
        selectedSheet.value = selectedBottomSheet
        coroutineScope.launch { sheet.show() }
    }
    val closeSheet: () -> Unit = {
        coroutineScope.launch { sheet.hide() }
    }

    NotificationPermissionRequest()

    ModalBottomSheetLayout(
        sheetState = sheet,
        sheetContent = {
            when (selectedSheet.value) {
                ReminderBottomSheet.NAVIGATE -> BottomSheetNavigate(
                    selectedIndex = viewModel.selectedNavigateIndex,
                    onSelected = {
                        viewModel.selectedNavigateIndex = it
                        closeSheet()
                    }
                )
                ReminderBottomSheet.SORT -> BottomSheetSort(
                    selectedIndex = viewModel.selectedSortIndex,
                    onSelected = {
                        viewModel.selectedSortIndex = it
                        closeSheet()
                    }
                )
            }
        },
        content = {
            ReminderListHomeScaffold(
                onNavigationMenu = { openSheet(ReminderBottomSheet.NAVIGATE) },
                onSort = { openSheet(ReminderBottomSheet.SORT) },
                selectedReminderList = viewModel.selectedReminderList,
                navigator = navigator,
                selectedReminderSortOrder = viewModel.selectedReminderSortOrder
            )
        }
    )
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ReminderListHomeScaffold(
    onNavigationMenu: () -> Unit,
    onSort: () -> Unit,
    selectedReminderList: ReminderList,
    navigator: DestinationsNavigator,
    selectedReminderSortOrder: ReminderSortOrder
) {
    val topBarTitle = when (selectedReminderList) {
        ReminderList.ACTIVE -> stringResource(R.string.active_reminders)
        ReminderList.ALL -> stringResource(R.string.all_reminders)
    }

    Scaffold(
        topBar = {
            ReminderListHomeTopBar(
                title = topBarTitle,
                onSearch = {}
            )
        },
        bottomBar = {
            ReminderListHomeBottomBar(
                onNavigationMenu = onNavigationMenu,
                onSort = onSort,
            )
        },
        content = {
            Crossfade(targetState = selectedReminderList) { reminderList ->
                when (reminderList) {
                    ReminderList.ACTIVE -> ReminderListActiveScreen(
                        navigator = navigator,
                        selectedReminderSortOrder = selectedReminderSortOrder
                    )
                    ReminderList.ALL -> ReminderListAllScreen(
                        navigator = navigator,
                        selectedReminderSortOrder = selectedReminderSortOrder
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
    onSearch: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                color = colorResource(R.color.on_primary)
            )
        },
        actions = {
            IconButton(onClick = onSearch) {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = stringResource(R.string.cd_top_app_bar_search),
                    tint = colorResource(R.color.on_primary)
                )
            }
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
                painter = painterResource(R.drawable.ic_menu),
                contentDescription = stringResource(R.string.cd_bottom_app_bar_menu),
                tint = colorResource(R.color.on_primary)
            )
        }

        Spacer(Modifier.weight(1f))

        IconButton(onClick = onSort) {
            Icon(
                painter = painterResource(R.drawable.ic_sort),
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
    MdcTheme {
        ReminderListHomeScreen(
            navigator = EmptyDestinationsNavigator,
        )
    }
}
