package dev.shorthouse.remindme.ui.search

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.ui.component.emptystate.EmptyStateSearchReminders
import dev.shorthouse.remindme.ui.component.searchbar.RemindMeSearchBar
import dev.shorthouse.remindme.ui.destinations.ReminderDetailsScreenDestination
import dev.shorthouse.remindme.ui.list.ReminderList

@Composable
@Destination
fun ReminderSearchScreen(
    viewModel: ReminderSearchViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ReminderSearchScreen(
        uiState = uiState,
        onNavigateUp = { navigator.navigateUp() },
        onSearchQueryChange = { viewModel.updateSearchQuery(it) },
        onNavigateDetails = {
            navigator.popBackStack()
            navigator.navigate(ReminderDetailsScreenDestination(it.id))
        },
        onCompleteReminder = { viewModel.completeReminder(it) }
    )
}

@Composable
fun ReminderSearchScreen(
    uiState: ReminderSearchUiState,
    onNavigateUp: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onNavigateDetails: (Reminder) -> Unit,
    onCompleteReminder: (Reminder) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            ReminderSearchTopBar(
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                onNavigateUp = onNavigateUp
            )
        },
        content = { scaffoldPadding ->
            if (!uiState.isLoading) {
                ReminderSearchContent(
                    reminders = uiState.searchReminders,
                    searchQuery = uiState.searchQuery,
                    onNavigateDetails = onNavigateDetails,
                    onCompleteReminder = onCompleteReminder,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(scaffoldPadding)
                )
            }
        },
        modifier = modifier
    )
}

@Composable
private fun ReminderSearchTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onNavigateUp: () -> Unit
) {
    RemindMeSearchBar(
        searchQuery = searchQuery,
        onSearchQueryChange = onSearchQueryChange,
        onCloseSearch = onNavigateUp
    )
}

@Composable
fun ReminderSearchContent(
    reminders: List<Reminder>,
    searchQuery: String,
    onNavigateDetails: (Reminder) -> Unit,
    onCompleteReminder: (Reminder) -> Unit,
    modifier: Modifier
) {
    ReminderList(
        reminders = reminders,
        emptyState = {
            if (searchQuery.isNotEmpty()) {
                EmptyStateSearchReminders(modifier = modifier)
            }
        },
        onReminderCard = onNavigateDetails,
        onCompleteReminder = onCompleteReminder,
        contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp),
        modifier = modifier
    )
}
