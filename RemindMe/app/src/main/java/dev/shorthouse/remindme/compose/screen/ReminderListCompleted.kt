package dev.shorthouse.remindme.compose.screen

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.component.EmptyStateCompletedReminders
import dev.shorthouse.remindme.compose.component.ReminderAlertDialog
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.theme.RemindMeTheme
import dev.shorthouse.remindme.util.enums.ReminderSortOrder
import dev.shorthouse.remindme.viewmodel.ListCompletedViewModel

@Destination
@Composable
fun ReminderListCompletedScreen(navigator: DestinationsNavigator) {
    val listCompletedViewModel: ListCompletedViewModel = hiltViewModel()

    var isDeleteDialogOpen by remember { mutableStateOf(false) }
    if (isDeleteDialogOpen) {
        ReminderAlertDialog(
            title = stringResource(R.string.alert_dialog_title_delete_completed),
            confirmText = stringResource(R.string.dialog_action_delete),
            onConfirm = {
                isDeleteDialogOpen = false
                listCompletedViewModel.deleteSelectedReminder()
            },
            onDismiss = { isDeleteDialogOpen = false }
        )
    }

    var reminderListSortOrder by remember { mutableStateOf(ReminderSortOrder.EARLIEST_DATE_FIRST) }

    val completedReminderStates = listCompletedViewModel
        .getCompletedReminderStates(reminderListSortOrder)
        .observeAsState()

    completedReminderStates.value?.let {
        ReminderListCompletedScaffold(
            reminderStates = it,
            onNavigateUp = { navigator.navigateUp() },
            onDeleteAll = { listCompletedViewModel.deleteCompletedReminders() },
            onReminderCard = { reminderState ->
                listCompletedViewModel.selectedReminderState = reminderState
                isDeleteDialogOpen = true
            },
            emptyStateContent = {
                EmptyStateCompletedReminders(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.surface)
                )
            }
        )
    }
}

@Composable
fun ReminderListCompletedScaffold(
    reminderStates: List<ReminderState>,
    onNavigateUp: () -> Unit,
    onDeleteAll: () -> Unit,
    onReminderCard: (ReminderState) -> Unit,
    emptyStateContent: @Composable () -> Unit,
) {
    Scaffold(
        topBar = {
            ReminderListCompletedTopBar(
                onNavigateUp = onNavigateUp,
                onDeleteAll = onDeleteAll
            )
        },
        content = { scaffoldPadding ->
            val modifier = Modifier.padding(scaffoldPadding)

            ReminderListContent(
                reminderStates = reminderStates,
                emptyStateContent = emptyStateContent,
                onReminderCard = onReminderCard,
                modifier = modifier
            )
        }
    )
}

@Composable
fun ReminderListCompletedTopBar(
    onNavigateUp: () -> Unit,
    onDeleteAll: () -> Unit
) {
    var isDeleteAllDialogOpen by remember { mutableStateOf(false) }
    if (isDeleteAllDialogOpen) {
        ReminderAlertDialog(
            title = stringResource(R.string.alert_dialog_title_delete_all_completed),
            confirmText = stringResource(R.string.dialog_action_delete),
            onConfirm = {
                isDeleteAllDialogOpen = false
                onDeleteAll()
            },
            onDismiss = { isDeleteAllDialogOpen = false }
        )
    }

    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.completed_reminders),
                style = MaterialTheme.typography.h6,
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.cd_top_app_bar_back),
                )
            }
        },
        actions = {
            IconButton(onClick = { isDeleteAllDialogOpen = true }) {
                Icon(
                    imageVector = Icons.Rounded.DeleteOutline,
                    contentDescription = stringResource(R.string.cd_top_bar_delete_completed_reminders),
                    tint = MaterialTheme.colors.onPrimary
                )
            }
        }
    )
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ReminderListCompletedPreview() {
    RemindMeTheme {
        ReminderListCompletedScaffold(
            reminderStates = emptyList(),
            onNavigateUp = {},
            onDeleteAll = {},
            emptyStateContent = {},
            onReminderCard = {}
        )
    }
}
