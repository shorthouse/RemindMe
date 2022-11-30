package dev.shorthouse.remindme.compose

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.google.android.material.composethemeadapter.MdcTheme
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.model.DisplayReminder
import dev.shorthouse.remindme.model.DisplayRepeatInterval
import dev.shorthouse.remindme.viewmodel.DetailsViewModel

@Composable
fun ReminderDetails(detailsViewModel: DetailsViewModel) {
    val reminder by detailsViewModel.displayReminder.observeAsState()

    reminder?.let {
        ReminderDetailsScaffold(it)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderDetailsScaffold(reminder: DisplayReminder) {
    Scaffold(
        topBar = { ReminderDetailsTopAppBar() },
        content = { innerPadding -> ReminderDetailsContent(reminder, innerPadding) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderDetailsTopAppBar() {
    var isMenuShown by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.toolbar_title_details),
                color = colorResource(R.color.white)
            )
        },
        navigationIcon = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    painter = painterResource(R.drawable.ic_back),
                    contentDescription = stringResource(R.string.cd_back),
                    tint = colorResource(R.color.white)
                )
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(colorResource(R.color.primary)),
        actions = {
            IconButton(onClick = { /*TODO*/ }) {
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
                    onClick = { /*TODO*/ },
                    text = { Text(text = stringResource(R.string.menu_item_delete)) }
                )
                DropdownMenuItem(
                    onClick = { /*TODO*/ },
                    text = { Text(text = stringResource(R.string.menu_item_complete)) }
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
            .padding(
                start = dimensionResource(R.dimen.margin_normal),
                end = dimensionResource(R.dimen.margin_normal),
                top = innerPadding.calculateTopPadding() + dimensionResource(R.dimen.margin_normal),
            )
            .verticalScroll(rememberScrollState())
    ) {
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

        if (displayReminder.isNotificationSent) {
            ReminderDetailRow(
                R.drawable.ic_notification_outline,
                R.string.cd_icon_notification,
                stringResource(R.string.notifications_on),
            )
        }

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
    cancelText: String,
    showInitially: Boolean = false,
) {
    var isDialogOpen by remember { mutableStateOf(showInitially) }

    if (isDialogOpen) {
        AlertDialog(
            title = {
                Text(text = title, fontSize = 18.sp)
            },
            confirmButton = {
                TextButton(onClick = { isDialogOpen = false }) {
                    Text(text = confirmText, fontSize = 16.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { isDialogOpen = false }) {
                    Text(text = cancelText, fontSize = 16.sp)
                }
            },
            onDismissRequest = { isDialogOpen = false },
        )
    }
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

        ReminderDetailsScaffold(reminder = reminder)
    }
}

@Preview()
@Composable
private fun DeleteAlertDialogPreview() {
    MdcTheme {
        DetailsAlertDialog(
            title = "Delete this reminder?",
            confirmText = "Delete",
            cancelText = "Cancel",
            showInitially = true
        )
    }
}