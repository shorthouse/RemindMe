package dev.shorthouse.remindme.compose.screen

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.component.ReminderAlertDialog
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.viewmodel.DetailsViewModel

@Destination
@Composable
fun ReminderCompletedDetailsScreen(
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

        ReminderCompletedDetailsScaffold(
            reminderState = ReminderState(it),
            onDelete = onDelete,
            navigator = navigator
        )
    }
}

@Composable
fun ReminderCompletedDetailsScaffold(
    reminderState: ReminderState,
    onDelete: () -> Unit,
    navigator: DestinationsNavigator
) {
    Scaffold(
        topBar = {
            ReminderCompletedDetailsTopBar(
                navigator = navigator,
                onDelete = onDelete,
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
fun ReminderCompletedDetailsTopBar(
    onDelete: () -> Unit,
    navigator: DestinationsNavigator
) {
    var isDeleteDialogShown by remember { mutableStateOf(false) }

    if (isDeleteDialogShown) {
        ReminderAlertDialog(
            title = stringResource(R.string.alert_dialog_title_delete),
            confirmText = stringResource(R.string.dialog_action_delete),
            onConfirm = onDelete,
            onDismiss = { isDeleteDialogShown = false }
        )
    }

    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.top_bar_title_details),
                color = colorResource(R.color.on_primary),
                style = MaterialTheme.typography.h5
            )
        },
        navigationIcon = {
            IconButton(onClick = { navigator.navigateUp() }) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.cd_back),
                    tint = colorResource(R.color.on_primary)
                )
            }
        },
        actions = {
            IconButton(onClick = { isDeleteDialogShown = true }) {
                Icon(
                    painter = painterResource(R.drawable.ic_delete),
                    contentDescription = stringResource(R.string.menu_item_delete),
                    tint = colorResource(R.color.on_primary)
                )
            }
        }
    )
}
