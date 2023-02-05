package dev.shorthouse.remindme.compose.component.list

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.preview.PreviewData
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.theme.RemindMeTheme

@Composable
fun ReminderListContent(
    reminderStates: List<ReminderState>,
    emptyStateContent: @Composable () -> Unit,
    onReminderCard: (ReminderState) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (reminderStates.isEmpty()) {
        emptyStateContent()
    } else {
        ReminderList(
            reminderStates = reminderStates,
            onReminderCard = onReminderCard,
            modifier = modifier
        )
    }
}

@Composable
fun ReminderList(
    reminderStates: List<ReminderState>,
    onReminderCard: (ReminderState) -> Unit,
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
                onReminderCard = onReminderCard
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
            emptyStateContent = {},
            onReminderCard = {},
        )
    }
}
