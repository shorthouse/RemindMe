package dev.shorthouse.remindme.compose.screen

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
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
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.component.ReminderEmptyState
import dev.shorthouse.remindme.compose.component.ReminderListItem
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.viewmodel.ReminderListViewModel
import java.time.LocalTime

@Composable
fun ReminderListAllScreen(reminderListViewModel: ReminderListViewModel) {
    val allReminders by reminderListViewModel.allReminders.observeAsState()

    allReminders?.let { reminders ->
        val reminderStates = reminders.map { ReminderState(it) }
        ReminderListAll(reminderStates = reminderStates)
    }
}

@Composable
fun ReminderListAll(reminderStates: List<ReminderState>) {
    if (reminderStates.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxHeight()
        ) {
            ReminderEmptyState(
                painter = painterResource(R.drawable.ic_empty_state_all),
                title = stringResource(R.string.empty_state_all_title),
                subtitle = stringResource(R.string.empty_state_all_subtitle)
            )
        }

    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.margin_large)),
            contentPadding = PaddingValues(
                horizontal = dimensionResource(R.dimen.margin_normal),
                vertical = dimensionResource(R.dimen.margin_normal)
            ),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            items(reminderStates) { reminderState ->
                ReminderListItem(reminderState = reminderState)
            }
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ReminderListAllPreview() {
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

    ReminderListAll(reminderStates = reminderStates)
}
