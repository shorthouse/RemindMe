package dev.shorthouse.remindme.compose

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.material.composethemeadapter.MdcTheme
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.model.DisplayReminder
import dev.shorthouse.remindme.model.DisplayRepeatInterval
import dev.shorthouse.remindme.viewmodel.DetailsViewModel

@Composable
fun ReminderDetailsScreen(
    detailsViewModel: DetailsViewModel = viewModel(),
    onNavigateEdit: () -> Unit,
    onNavigateUp: () -> Unit
) {
    val reminder by detailsViewModel.displayReminder.observeAsState()

    reminder?.let {
        ReminderDetailsScreenContent(it, detailsViewModel, onNavigateEdit, onNavigateUp)
    }
}

@Composable
fun ReminderDetailsScreenContent(
    reminder: DisplayReminder,
    detailsViewModel: DetailsViewModel,
    onNavigateEdit: () -> Unit,
    onNavigateUp: () -> Unit
) {
    Scaffold(
        topBar = {
            ReminderDetailsTopAppBar(
                onEdit = onNavigateEdit,
                onDelete = {
                    detailsViewModel.deleteReminder()
                    onNavigateUp()
                },
                onComplete = {
                    detailsViewModel.completeReminder()
                    onNavigateUp()
                },
                onNavigateUp = onNavigateUp
            )
        },
        content = { innerPadding ->
            ReminderDetailsContent(reminder, innerPadding)
        }
    )
}

@Composable
fun ReminderDetailsTopAppBar(
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onComplete: () -> Unit,
    onNavigateUp: () -> Unit
) {
    var isMenuShown by remember { mutableStateOf(false) }
    var isDeleteDialogShown by remember { mutableStateOf(false) }
    var isCompleteDialogShown by remember { mutableStateOf(false) }

    if (isDeleteDialogShown) {
        DetailsAlertDialog(
            title = "Delete this reminder?",
            confirmText = "Delete",
            onConfirm = onDelete,
            onDismiss = { isDeleteDialogShown = false }
        )
    }

    if (isCompleteDialogShown) {
        DetailsAlertDialog(
            title = "Complete this reminder?",
            confirmText = "Complete",
            onConfirm = onComplete,
            onDismiss = { isDeleteDialogShown = false }
        )
    }

    TopAppBar(
        modifier = Modifier.testTag("TopAppBar"),
        title = {
            Text(
                text = stringResource(R.string.toolbar_title_details),
                color = colorResource(R.color.white)
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(
                    painter = painterResource(R.drawable.ic_back),
                    contentDescription = stringResource(R.string.cd_back),
                    tint = colorResource(R.color.white)
                )
            }
        },
        actions = {
            IconButton(onClick = onEdit) {
                Icon(
                    painter = painterResource(R.drawable.ic_edit),
                    contentDescription = stringResource(R.string.cd_menu_item_edit),
                    tint = colorResource(R.color.white)
                )
            }
            IconButton(onClick = { isMenuShown = !isMenuShown }) {
                Icon(
                    painter = painterResource(R.drawable.ic_more),
                    contentDescription = stringResource(R.string.cd_more),
                    tint = colorResource(R.color.white)
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ReminderDetailsContent(displayReminder: DisplayReminder, innerPadding: PaddingValues) {
    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(
                start = dimensionResource(R.dimen.margin_normal),
                end = dimensionResource(R.dimen.margin_normal),
                top = innerPadding.calculateTopPadding(),
            )
    ) {
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_normal)))

        ReminderName(displayReminder.name)

        ReminderDetailRow(
            R.drawable.ic_calendar,
            R.string.cd_icon_calendar,
            displayReminder.startDate
        )

        ReminderDetailRow(
            R.drawable.ic_clock,
            R.string.cd_icon_clock,
            displayReminder.startTime
        )

        if (displayReminder.repeatInterval != null) {
            ReminderDetailRow(
                R.drawable.ic_repeat,
                R.string.cd_icon_repeat,
                pluralStringResource(
                    displayReminder.repeatInterval.pluralId,
                    displayReminder.repeatInterval.pluralCount,
                    displayReminder.repeatInterval.pluralCount
                ),
            )
        }

        if (displayReminder.isNotificationSent) {
            ReminderDetailRow(
                R.drawable.ic_notification_outline,
                R.string.cd_icon_notification,
                stringResource(R.string.notifications_on),
            )
        }

        if (displayReminder.notes != null) {
            ReminderDetailRow(
                R.drawable.ic_notes,
                R.string.cd_icon_notes,
                displayReminder.notes
            )
        }

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_small)))
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

@Composable
fun ReminderDetailRow(
    @DrawableRes iconId: Int,
    @StringRes iconContentDescriptionId: Int,
    detailText: String,
) {
    Row(Modifier.padding(top = dimensionResource(R.dimen.margin_large))) {
        Image(
            painter = painterResource(iconId),
            contentDescription = stringResource(iconContentDescriptionId),
        )

        Text(
            text = detailText,
            fontSize = 18.sp,
            color = colorResource(R.color.on_primary),
            modifier = Modifier
                .padding(start = dimensionResource(R.dimen.margin_normal))
        )
    }
}

@Composable
private fun DetailsAlertDialog(
    title: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        title = {
            Text(text = title, fontSize = 18.sp)
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = confirmText, fontSize = 16.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.alert_dialog_cancel), fontSize = 16.sp)
            }
        },
        onDismissRequest = onDismiss,
    )
}

@Preview(showBackground = true)
@Composable
private fun ReminderDetailsScaffoldPreview() {
    MdcTheme {
        val reminder = DisplayReminder(
            id = 1,
            name = "Yoga tonight",
            startDate = "Wed, 22 Mar 2000",
            startTime = "14:30",
            isNotificationSent = true,
            repeatInterval = DisplayRepeatInterval(R.plurals.interval_weeks, 2),
            notes = "Don't forget to warm up!",
        )

        ReminderDetailsScreenContent(
            reminder = reminder,
            detailsViewModel = viewModel(),
            onNavigateEdit = {},
            onNavigateUp = {}
        )
    }
}

@Preview
@Composable
private fun DetailsAlertDialogPreview() {
    MdcTheme {
        DetailsAlertDialog(
            title = "Delete this reminder?",
            confirmText = "Delete",
            onConfirm = {},
            onDismiss = {}
        )
    }
}
