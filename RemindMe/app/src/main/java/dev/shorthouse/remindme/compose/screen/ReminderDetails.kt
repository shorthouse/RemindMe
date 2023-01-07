package dev.shorthouse.remindme.compose.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.material.composethemeadapter.MdcTheme
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.component.ReminderAlertDialog
import dev.shorthouse.remindme.compose.component.TextWithLeftIcon
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.viewmodel.DetailsViewModel
import java.time.LocalTime

@Composable
fun ReminderDetailsScreen(
    detailsViewModel: DetailsViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit,
    onEdit: () -> Unit,
) {
    val onDelete = {
        detailsViewModel.deleteReminder()
        onNavigateUp()
    }

    val onComplete = {
        detailsViewModel.completeReminder()
        onNavigateUp()
    }

    val reminder by detailsViewModel.reminder.observeAsState()

    reminder?.let {
        ReminderDetailsScaffold(
            reminderState = ReminderState(it),
            onNavigateUp = onNavigateUp,
            onEdit = onEdit,
            onDelete = onDelete,
            onComplete = onComplete
        )
    }
}

@Composable
fun ReminderDetailsScaffold(
    reminderState: ReminderState,
    onNavigateUp: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onComplete: () -> Unit,
) {
    Scaffold(
        topBar = {
            ReminderDetailsTopBar(
                onNavigateUp = onNavigateUp,
                onEdit = onEdit,
                onDelete = onDelete,
                onComplete = onComplete
            )
        },
        content = { innerPadding ->
            ReminderDetailsContent(
                innerPadding = innerPadding,
                reminderState = reminderState
            )
        }
    )
}

@Composable
fun ReminderDetailsTopBar(
    onNavigateUp: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onComplete: () -> Unit
) {
    var isMenuShown by remember { mutableStateOf(false) }
    var isDeleteDialogShown by remember { mutableStateOf(false) }
    var isCompleteDialogShown by remember { mutableStateOf(false) }

    if (isDeleteDialogShown) {
        ReminderAlertDialog(
            title = stringResource(R.string.alert_dialog_title_delete),
            confirmText = stringResource(R.string.alert_dialog_confirm_delete),
            onConfirm = onDelete,
            onDismiss = { isDeleteDialogShown = false }
        )
    }

    if (isCompleteDialogShown) {
        ReminderAlertDialog(
            title = stringResource(R.string.alert_dialog_title_complete),
            confirmText = stringResource(R.string.alert_dialog_confirm_complete),
            onConfirm = onComplete,
            onDismiss = { isCompleteDialogShown = false }
        )
    }

    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.toolbar_title_details),
                color = colorResource(R.color.on_primary)
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(
                    painter = painterResource(R.drawable.ic_back),
                    contentDescription = stringResource(R.string.cd_back),
                    tint = colorResource(R.color.on_primary)
                )
            }
        },
        actions = {
            IconButton(onClick = onEdit) {
                Icon(
                    painter = painterResource(R.drawable.ic_edit),
                    contentDescription = stringResource(R.string.cd_menu_item_edit),
                    tint = colorResource(R.color.on_primary)
                )
            }
            IconButton(onClick = { isMenuShown = !isMenuShown }) {
                Icon(
                    painter = painterResource(R.drawable.ic_more),
                    contentDescription = stringResource(R.string.cd_more),
                    tint = colorResource(R.color.on_primary)
                )
            }
            DropdownMenu(
                expanded = isMenuShown,
                onDismissRequest = { isMenuShown = false }
            ) {
                DropdownMenuItem(
                    onClick = {
                        isDeleteDialogShown = true
                        isMenuShown = false
                    },
                    content = { Text(text = stringResource(R.string.menu_item_delete)) }
                )
                DropdownMenuItem(
                    onClick = {
                        isCompleteDialogShown = true
                        isMenuShown = false
                    },
                    content = { Text(text = stringResource(R.string.menu_item_complete)) }
                )
            }
        }
    )
}

@Composable
fun ReminderDetailsContent(
    innerPadding: PaddingValues,
    reminderState: ReminderState
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.margin_large)),
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(
                start = dimensionResource(R.dimen.margin_normal),
                top = innerPadding.calculateTopPadding() + dimensionResource(R.dimen.margin_large),
                end = dimensionResource(R.dimen.margin_normal),
                bottom = dimensionResource(R.dimen.margin_large)
            )
    ) {
        ReminderName(
            name = reminderState.name,
        )

        val detailIcons = buildList {
            add(painterResource(R.drawable.ic_calendar))
            add(painterResource(R.drawable.ic_clock))
            if (reminderState.isRepeatReminder) add(painterResource(R.drawable.ic_repeat))
            if (reminderState.isNotificationSent) add(painterResource(R.drawable.ic_notification_outline))
            reminderState.notes?.let { add(painterResource(R.drawable.ic_notes)) }
        }

        val detailTexts = buildList {
            add(reminderState.date)
            add(reminderState.time.toString())
            if (reminderState.isRepeatReminder) add(
                stringResource(
                    R.string.reminder_details_repeat_interval,
                    reminderState.repeatAmount,
                    reminderState.repeatUnit
                )
            )
            if (reminderState.isNotificationSent) add(stringResource(R.string.notifications_on))
            reminderState.notes?.let { add(it) }
        }

        repeat(detailIcons.size) { i ->
            TextWithLeftIcon(
                icon = detailIcons[i],
                text = detailTexts[i],
            )
        }
    }
}

@Composable
fun ReminderName(name: String) {
    Text(
        text = name,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
    )
}

@Preview(showBackground = true)
@Composable
private fun ReminderDetailsScreenPreview() {
    MdcTheme {
        val reminderState by remember {
            mutableStateOf(
                ReminderState(
                    id = 1,
                    name = "Yoga tonight",
                    date = "Wed, 22 Mar 2000",
                    time = LocalTime.of(14, 30),
                    isNotificationSent = true,
                    isRepeatReminder = true,
                    repeatAmount = "2",
                    repeatUnit = "Weeks",
                    notes = "Don't forget to warm up!"
                )
            )
        }

        ReminderDetailsScaffold(
            reminderState = reminderState,
            onNavigateUp = {},
            onEdit = {},
            onDelete = {},
            onComplete = {}
        )
    }
}

@Preview
@Composable
private fun DetailsAlertDialogPreview() {
    MdcTheme {
        ReminderAlertDialog(
            title = stringResource(R.string.alert_dialog_title_delete),
            confirmText = stringResource(R.string.alert_dialog_confirm_delete),
            onConfirm = {},
            onDismiss = {}
        )
    }
}
