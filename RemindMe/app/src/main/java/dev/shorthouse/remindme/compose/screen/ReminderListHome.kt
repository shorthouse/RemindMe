package dev.shorthouse.remindme.compose.screen

import android.content.res.Configuration
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.material.composethemeadapter.MdcTheme
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.component.NavigationBottomSheet
import dev.shorthouse.remindme.compose.component.SortBottomSheet
import dev.shorthouse.remindme.utilities.enums.ReminderBottomSheet
import kotlinx.coroutines.launch

@Composable
fun ReminderListHomeScreen() {
    ReminderListHome()
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ReminderListHome() {
    val modalBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()
    val selectedBottomSheetState = remember { mutableStateOf(ReminderBottomSheet.NAVIGATE) }

    ModalBottomSheetLayout(
        sheetState = modalBottomSheetState,
        sheetContent = {
            when (selectedBottomSheetState.value) {
                ReminderBottomSheet.NAVIGATE -> BottomSheetNavigate()
                ReminderBottomSheet.SORT -> BottomSheetSort()
            }
        },
        scrimColor = Color.Black.copy(alpha = 0.32f),
    ) {
        ReminderListHomeScaffold(
            onNavigationMenu = {
                selectedBottomSheetState.value = ReminderBottomSheet.NAVIGATE
                coroutineScope.launch { modalBottomSheetState.show() }
            },
            onSort = {
                selectedBottomSheetState.value = ReminderBottomSheet.SORT
                coroutineScope.launch { modalBottomSheetState.show() }
            }
        )
    }
}

@Composable
fun BottomSheetNavigate() {
    NavigationBottomSheet()
}

@Composable
fun BottomSheetSort() {
    Surface(modifier = Modifier.fillMaxWidth()) {
        SortBottomSheet()
    }
}

@Composable
fun ReminderListHomeScaffold(
    onNavigationMenu: () -> Unit,
    onSort: () -> Unit
) {
    Scaffold(
        topBar = {
            ReminderListHomeTopBar({})
        },
        bottomBar = {
            ReminderListHomeBottomBar(
                onNavigationMenu = onNavigationMenu,
                onSort = onSort
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.cd_top_app_bar_search)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,
        content = { scaffoldPadding ->
            ReminderListHomeContent(modifier = Modifier.padding(scaffoldPadding))
        },
    )
}

@Composable
fun ReminderListHomeTopBar(
    onSearch: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = "Home list",
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

@Composable
fun ReminderListHomeContent(modifier: Modifier = Modifier) {
    Text("the list home content!")
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ReminderListHomePreview() {
    MdcTheme {
        ReminderListHome()
    }
}
