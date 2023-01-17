package dev.shorthouse.remindme.compose.screen

import android.annotation.SuppressLint
import android.content.res.Configuration
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
import com.google.android.material.composethemeadapter.MdcTheme
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.component.BottomSheetNavigate
import dev.shorthouse.remindme.compose.component.BottomSheetSort
import dev.shorthouse.remindme.utilities.enums.ReminderBottomSheet
import dev.shorthouse.remindme.utilities.enums.ReminderList
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ReminderListHomeScreen(
    onNavigateAdd: () -> Unit,
    onNavigateDetails: (Long) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val selectedSheetState = remember { mutableStateOf(ReminderBottomSheet.NAVIGATE) }
    var selectedNavigateIndex by remember { mutableStateOf(0) }
    var selectedSortIndex by remember { mutableStateOf(0) }
    val selectedReminderList by remember {
        derivedStateOf {
            if (selectedNavigateIndex == 0) ReminderList.ACTIVE else ReminderList.ALL
        }
    }

    val coroutineScope = rememberCoroutineScope()
    val openSheet: (ReminderBottomSheet) -> Unit = { selectedBottomSheet ->
        selectedSheetState.value = selectedBottomSheet
        coroutineScope.launch { sheetState.show() }
    }
    val closeSheet: () -> Unit = {
        coroutineScope.launch { sheetState.hide() }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            when (selectedSheetState.value) {
                ReminderBottomSheet.NAVIGATE -> BottomSheetNavigate(
                    selectedIndex = selectedNavigateIndex,
                    onSelected = {
                        selectedNavigateIndex = it
                        closeSheet()
                    }
                )
                ReminderBottomSheet.SORT -> BottomSheetSort(
                    selectedIndex = selectedSortIndex,
                    onSelected = {
                        selectedSortIndex = it
                        closeSheet()
                    }
                )
            }
        },
        content = {
            ReminderListHomeScaffold(
                onNavigationMenu = { openSheet(ReminderBottomSheet.NAVIGATE) },
                onSort = { openSheet(ReminderBottomSheet.SORT) },
                selectedReminderList = selectedReminderList,
                onNavigateAdd = onNavigateAdd,
                onNavigateDetails = onNavigateDetails
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
    onNavigateAdd: () -> Unit,
    onNavigateDetails: (Long) -> Unit
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
            when (selectedReminderList) {
                ReminderList.ACTIVE -> ReminderListActiveScreen(onNavigateDetails = onNavigateDetails)
                ReminderList.ALL -> ReminderListAllScreen(onNavigateDetails = onNavigateDetails)
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
        ReminderListHomeScreen({}, {})
    }
}
