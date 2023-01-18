package dev.shorthouse.remindme.compose.screen

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.component.ActiveReminderListItem
import dev.shorthouse.remindme.compose.component.ReminderEmptyState
import dev.shorthouse.remindme.compose.screen.destinations.ReminderDetailsScreenDestination
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.viewmodel.ListActiveViewModel
import java.time.LocalTime

@Composable
fun ReminderListActiveScreen(
    listActiveViewModel: ListActiveViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val activeReminders by listActiveViewModel.activeReminders.observeAsState()

    val onCompleteChecked: (Reminder) -> Unit = { reminder ->
        listActiveViewModel.updateDoneReminder(reminder)
        listActiveViewModel.removeDisplayingNotification(reminder)

        //TODO
        // Show the undo snackbar - will already have padding for the list so this is fine
    }

    activeReminders?.let { reminders ->
        val reminderStates = reminders.map { ReminderState(it) }

        ReminderListActiveContent(
            reminderStates = reminderStates,
            onCompleteChecked = onCompleteChecked,
            navigator = navigator
        )
    }
}

@Composable
fun ReminderListActiveContent(
    reminderStates: List<ReminderState>,
    onCompleteChecked: (Reminder) -> Unit,
    navigator: DestinationsNavigator
) {
    if (reminderStates.isEmpty()) {
        ReminderEmptyState(
            painter = painterResource(R.drawable.ic_empty_state_active),
            title = stringResource(R.string.empty_state_active_title),
            subtitle = stringResource(R.string.empty_state_active_subtitle)
        )
    } else {
        ReminderListActive(
            reminderStates = reminderStates,
            onCompleteChecked = onCompleteChecked,
            navigator = navigator
        )
    }
}

@Composable
private fun ReminderListActive(
    reminderStates: List<ReminderState>,
    onCompleteChecked: (Reminder) -> Unit,
    navigator: DestinationsNavigator,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.margin_large)),
        contentPadding = PaddingValues(
            start = dimensionResource(R.dimen.margin_normal),
            top = dimensionResource(R.dimen.margin_normal),
            end = dimensionResource(R.dimen.margin_normal),
            bottom = dimensionResource(R.dimen.margin_bottom_bar),
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(reminderStates) { reminderState ->
            ActiveReminderListItem(
                reminderState = reminderState,
                onCompleteChecked = onCompleteChecked,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navigator.navigate(ReminderDetailsScreenDestination(reminderId = reminderState.id)) })
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ReminderListActivePreview() {
    val reminderStates = listOf(
        ReminderState(
            id = 1,
            name = "Yoga with Alice",
            date = "Wed, 22 Mar 2000",
            time = LocalTime.of(14, 30),
            isNotificationSent = true,
            isRepeatReminder = true,
            repeatAmount = "2",
            repeatUnit = "Weeks",
            notes = "Don't forget to warm up!"
        ),
        ReminderState(
            id = 2,
            name = "Feed the fish",
            date = "Fri, 27 Jun 2017",
            time = LocalTime.of(18, 15),
            isNotificationSent = true,
            isRepeatReminder = false,
            repeatAmount = "",
            repeatUnit = "",
            notes = null
        ),
        ReminderState(
            id = 3,
            name = "Go for a run",
            date = "Wed, 07 Jan 2022",
            time = LocalTime.of(7, 15),
            isNotificationSent = false,
            isRepeatReminder = false,
            repeatAmount = "",
            repeatUnit = "",
            notes = "The cardio will be worth it!"
        )
    )

    ReminderListActiveContent(
        reminderStates = reminderStates,
        navigator = EmptyDestinationsNavigator,
        onCompleteChecked = {}
    )
}
