package dev.shorthouse.remindme.compose.screen

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.component.EmptyStateCompletedReminders
import dev.shorthouse.remindme.compose.component.EmptyStateScheduledReminders
import dev.shorthouse.remindme.compose.component.ReminderListCard
import dev.shorthouse.remindme.compose.preview.PreviewData
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.theme.RemindMeTheme
import dev.shorthouse.remindme.util.enums.ReminderList
import dev.shorthouse.remindme.util.enums.ReminderSortOrder
import dev.shorthouse.remindme.viewmodel.ListViewModel

@Composable
fun ReminderListScreen(
    selectedReminderList: ReminderList,
    selectedReminderSortOrder: ReminderSortOrder,
    onReminderActions: (ReminderState) -> Unit,
    modifier: Modifier = Modifier
) {
    val reminderListViewModel: ListViewModel = hiltViewModel()

    val reminderStates by reminderListViewModel
        .getReminderStates(
            selectedReminderList,
            selectedReminderSortOrder
        )
        .observeAsState()

    reminderStates?.let {
        ReminderListContent(
            reminderStates = it,
            selectedReminderList = selectedReminderList,
            onReminderActions = onReminderActions,
            modifier = modifier
        )
    }
}

@Composable
fun ReminderListContent(
    reminderStates: List<ReminderState>,
    selectedReminderList: ReminderList,
    onReminderActions: (ReminderState) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (reminderStates.isNotEmpty()) {
        ReminderList(
            reminderStates = reminderStates,
            onReminderActions = onReminderActions,
        )
    } else {
        val emptyStateModifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface)

        when (selectedReminderList) {
            ReminderList.SCHEDULED -> EmptyStateScheduledReminders(modifier = emptyStateModifier)
            ReminderList.COMPLETED -> EmptyStateCompletedReminders(modifier = emptyStateModifier)
        }
    }
}

@Composable
fun ReminderList(
    reminderStates: List<ReminderState>,
    onReminderActions: (ReminderState) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.margin_tiny)),
        contentPadding = PaddingValues(
            start = dimensionResource(R.dimen.margin_tiny),
            top = dimensionResource(R.dimen.margin_tiny),
            end = dimensionResource(R.dimen.margin_tiny),
            bottom = dimensionResource(R.dimen.margin_bottom_bar)
        ),
        modifier = modifier.fillMaxSize()
    ) {
        items(reminderStates) { reminderState ->
            ReminderListCard(
                reminderState = reminderState,
                onReminderActions = onReminderActions
            )
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ReminderListContentPreview() {
    RemindMeTheme {
        val reminderStates = PreviewData.previewReminderStates

        ReminderListContent(
            reminderStates = reminderStates,
            selectedReminderList = ReminderList.SCHEDULED,
            onReminderActions = {},
        )
    }
}
