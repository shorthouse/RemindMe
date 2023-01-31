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
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.component.CompletedReminderListItem
import dev.shorthouse.remindme.compose.component.EmptyStateCompletedReminders
import dev.shorthouse.remindme.compose.preview.PreviewData
import dev.shorthouse.remindme.compose.screen.destinations.ReminderDetailsScreenDestination
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.theme.RemindMeTheme
import dev.shorthouse.remindme.utilities.enums.ReminderSortOrder
import dev.shorthouse.remindme.viewmodel.ListCompletedViewModel

@Composable
fun ReminderListCompletedScreen(
    navigator: DestinationsNavigator,
    selectedReminderSortOrder: ReminderSortOrder,
    modifier: Modifier = Modifier
) {
    val reminderListViewModel: ListCompletedViewModel = hiltViewModel()

    val completedReminders by reminderListViewModel.getCompletedReminders(selectedReminderSortOrder).observeAsState()

    val onNavigateDetails: (Long) -> Unit = { reminderId ->
        navigator.navigate(ReminderDetailsScreenDestination(reminderId = reminderId))
    }

    completedReminders?.let { reminders ->
        val reminderStates = reminders.map { ReminderState(it) }

        ReminderListCompletedContent(
            reminderStates = reminderStates,
            onNavigateDetails = onNavigateDetails,
            modifier = modifier
        )
    }
}

@Composable
fun ReminderListCompletedContent(
    reminderStates: List<ReminderState>,
    onNavigateDetails: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    if (reminderStates.isEmpty()) {
        EmptyStateCompletedReminders(modifier = modifier.fillMaxSize())
    } else {
        ReminderListComplete(
            reminderStates = reminderStates,
            onNavigateDetails = onNavigateDetails
        )
    }
}

@Composable
private fun ReminderListComplete(
    reminderStates: List<ReminderState>,
    onNavigateDetails: (Long) -> Unit,
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
            CompletedReminderListItem(
                reminderState = reminderState,
                modifier = Modifier.clickable { onNavigateDetails(reminderState.id) })
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ReminderListCompletePreview() {
    RemindMeTheme {
        val reminderStates = PreviewData.previewReminderStates

        ReminderListCompletedContent(
            reminderStates = reminderStates,
            onNavigateDetails = {}
        )
    }
}
