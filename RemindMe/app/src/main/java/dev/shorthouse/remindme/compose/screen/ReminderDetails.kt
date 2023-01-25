package dev.shorthouse.remindme.compose.screen

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.component.ReminderAlertDialog
import dev.shorthouse.remindme.compose.component.TextWithLeftIcon
import dev.shorthouse.remindme.compose.screen.destinations.ReminderEditScreenDestination
import dev.shorthouse.remindme.compose.state.PreviewData
import dev.shorthouse.remindme.compose.state.ReminderDetailItem
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.theme.RemindMeTheme
import dev.shorthouse.remindme.viewmodel.DetailsViewModel

@Destination
@Composable
fun ReminderDetailsScreen(
    reminderId: Long,
    navigator: DestinationsNavigator
) {
    val detailsViewModel: DetailsViewModel = hiltViewModel()

    val reminder by detailsViewModel.getReminderDetails(reminderId).observeAsState()

    reminder?.let {
        val onDelete: () -> Unit = {
            detailsViewModel.deleteReminder(it)
            navigator.navigateUp()
        }

        val onComplete: () -> Unit = {
            detailsViewModel.completeReminder(it)
            navigator.navigateUp()
        }

        ReminderDetailsScaffold(
            reminderState = ReminderState(it),
            onDelete = onDelete,
            onComplete = onComplete,
            navigator = navigator
        )
    }
}

@Composable
fun ReminderDetailsScaffold(
    reminderState: ReminderState,
    onDelete: () -> Unit,
    onComplete: () -> Unit,
    navigator: DestinationsNavigator
) {
    Scaffold(
        topBar = {
            ReminderDetailsTopBar(
                reminderState = reminderState,
                navigator = navigator,
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
    reminderState: ReminderState,
    onDelete: () -> Unit,
    onComplete: () -> Unit,
    navigator: DestinationsNavigator
) {
    var isMenuShown by remember { mutableStateOf(false) }
    var isDeleteDialogShown by remember { mutableStateOf(false) }
    var isCompleteDialogShown by remember { mutableStateOf(false) }

    if (isDeleteDialogShown) {
        ReminderAlertDialog(
            title = stringResource(R.string.alert_dialog_title_delete),
            confirmText = stringResource(R.string.dialog_action_delete),
            onConfirm = onDelete,
            onDismiss = { isDeleteDialogShown = false }
        )
    }

    if (isCompleteDialogShown) {
        ReminderAlertDialog(
            title = stringResource(R.string.alert_dialog_title_complete),
            confirmText = stringResource(R.string.dialog_action_complete),
            onConfirm = onComplete,
            onDismiss = { isCompleteDialogShown = false }
        )
    }

    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.top_bar_title_details),
                style = MaterialTheme.typography.h5
            )
        },
        navigationIcon = {
            IconButton(onClick = { navigator.navigateUp() }) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.cd_back),
                    tint = MaterialTheme.colors.onPrimary
                )
            }
        },
        actions = {
            IconButton(onClick = { navigator.navigate(ReminderEditScreenDestination(reminderId = reminderState.id)) }) {
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = stringResource(R.string.menu_item_edit),
                    tint = MaterialTheme.colors.onPrimary
                )
            }
            IconButton(onClick = { isMenuShown = !isMenuShown }) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = stringResource(R.string.cd_more),
                    tint = MaterialTheme.colors.onPrimary
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
                    content = {
                        Text(
                            text = stringResource(R.string.menu_item_delete),
                            style = MaterialTheme.typography.body1
                        )
                    }
                )
                DropdownMenuItem(
                    onClick = {
                        isCompleteDialogShown = true
                        isMenuShown = false
                    },
                    content = {
                        Text(
                            text = stringResource(R.string.menu_item_complete),
                            style = MaterialTheme.typography.body1
                        )
                    }
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
        Text(
            text = reminderState.name,
            style = MaterialTheme.typography.h6
        )

        val detailItems = buildList {
            add(
                ReminderDetailItem(
                    Icons.Rounded.CalendarToday,
                    reminderState.date
                )
            )
            add(
                ReminderDetailItem(
                    Icons.Rounded.Schedule,
                    reminderState.time.toString()
                )
            )
            if (reminderState.isNotificationSent) {
                add(
                    ReminderDetailItem(
                        Icons.Rounded.NotificationsNone,
                        stringResource(R.string.notifications_on)
                    )
                )
            }
            if (reminderState.isRepeatReminder) {
                add(
                    ReminderDetailItem(
                        Icons.Rounded.Refresh,
                        stringResource(
                            R.string.reminder_details_repeat_interval,
                            reminderState.repeatAmount,
                            reminderState.repeatUnit
                        )
                    )
                )
            }
            reminderState.notes?.let { notes ->
                add(
                    ReminderDetailItem(
                        Icons.Rounded.Notes,
                        notes
                    )
                )
            }
        }

        detailItems.forEach { detailItem ->
            TextWithLeftIcon(
                icon = detailItem.icon,
                text = detailItem.label,
            )
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun ReminderDetailsScreenPreview() {
    RemindMeTheme {
        val reminderState by remember { mutableStateOf(PreviewData.reminderState) }

        ReminderDetailsScaffold(
            reminderState = reminderState,
            onDelete = {},
            onComplete = {},
            navigator = EmptyDestinationsNavigator
        )
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun ReminderDetailsCompleteDialogPreview() {
    RemindMeTheme {
        ReminderAlertDialog(
            title = stringResource(R.string.alert_dialog_title_complete),
            confirmText = stringResource(R.string.dialog_action_complete),
            onConfirm = {},
            onDismiss = {}
        )
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun ReminderDetailsDeleteDialogPreview() {
    RemindMeTheme {
        ReminderAlertDialog(
            title = stringResource(R.string.alert_dialog_title_delete),
            confirmText = stringResource(R.string.dialog_action_delete),
            onConfirm = {},
            onDismiss = {}
        )
    }
}
