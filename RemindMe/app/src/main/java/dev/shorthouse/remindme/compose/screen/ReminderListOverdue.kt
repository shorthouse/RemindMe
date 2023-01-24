package dev.shorthouse.remindme.compose.screen

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.component.EmptyStateOverdueReminders
import dev.shorthouse.remindme.compose.component.OverdueReminderListItem
import dev.shorthouse.remindme.compose.screen.destinations.ReminderDetailsScreenDestination
import dev.shorthouse.remindme.compose.state.PreviewData
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.theme.RemindMeTheme
import dev.shorthouse.remindme.utilities.enums.ReminderSortOrder
import dev.shorthouse.remindme.viewmodel.ListOverdueViewModel

@Composable
fun ReminderListOverdueScreen(
    navigator: DestinationsNavigator,
    selectedReminderSortOrder: ReminderSortOrder,
    modifier: Modifier = Modifier
) {
    val listOverdueViewModel: ListOverdueViewModel = hiltViewModel()

    val overdueReminders by listOverdueViewModel.getOverdueReminders(selectedReminderSortOrder).observeAsState()

    val onCompleteChecked: (Reminder) -> Unit = { reminder ->
        listOverdueViewModel.updateDoneReminder(reminder)
        listOverdueViewModel.removeDisplayingNotification(reminder)
    }

    overdueReminders?.let { reminders ->
        val reminderStates = reminders.map { ReminderState(it) }

        ReminderListOverdueContent(
            reminderStates = reminderStates,
            onCompleteChecked = onCompleteChecked,
            navigator = navigator,
            modifier = modifier
        )
    }
}

@Composable
fun ReminderListOverdueContent(
    reminderStates: List<ReminderState>,
    onCompleteChecked: (Reminder) -> Unit,
    navigator: DestinationsNavigator,
    modifier: Modifier = Modifier
) {
    if (reminderStates.isEmpty()) {
        EmptyStateOverdueReminders(modifier = modifier.fillMaxSize())
    } else {
        ReminderListOverdue(
            reminderStates = reminderStates,
            onCompleteChecked = onCompleteChecked,
            navigator = navigator
        )
    }
}

@Composable
private fun ReminderListOverdue(
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
            OverdueReminderListItem(
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
fun ReminderListOverduePreview() {
    RemindMeTheme {
        val reminderStates = PreviewData.reminderStateList

        ReminderListOverdueContent(
            reminderStates = reminderStates,
            navigator = EmptyDestinationsNavigator,
            onCompleteChecked = {}
        )
    }

}
