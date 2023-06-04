package dev.shorthouse.remindme.ui.screen.search

import android.content.res.Configuration
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.ui.component.emptystate.EmptyStateSearchReminders
import dev.shorthouse.remindme.ui.component.progressindicator.CenteredCircularProgressIndicator
import dev.shorthouse.remindme.ui.component.searchbar.RemindMeSearchBar
import dev.shorthouse.remindme.ui.previewprovider.ReminderListProvider
import dev.shorthouse.remindme.ui.screen.destinations.ReminderDetailsScreenDestination
import dev.shorthouse.remindme.ui.screen.list.ReminderList
import dev.shorthouse.remindme.ui.theme.AppTheme
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Destination
fun ReminderSearchScreen(
    navigator: DestinationsNavigator,
    viewModel: ReminderSearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current

    ReminderSearchScreen(
        uiState = uiState,
        onNavigateUp = {
            keyboardController?.hide()
            navigator.navigateUp()
        },
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
    modifier: Modifier = Modifier
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
            if (uiState.isLoading) {
                CenteredCircularProgressIndicator()
            } else {
                ReminderSearchContent(
                    reminders = uiState.searchReminders,
                    searchQuery = uiState.searchQuery,
                    onNavigateDetails = onNavigateDetails,
                    onCompleteReminder = onCompleteReminder,
                    contentPadding = scaffoldPadding
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
    reminders: ImmutableList<Reminder>,
    searchQuery: String,
    onNavigateDetails: (Reminder) -> Unit,
    onCompleteReminder: (Reminder) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    ReminderList(
        reminders = reminders,
        emptyState = {
            if (searchQuery.isNotEmpty()) {
                EmptyStateSearchReminders(modifier = Modifier.padding(contentPadding))
            }
        },
        onReminderCard = onNavigateDetails,
        onCompleteReminder = onCompleteReminder,
        contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp),
        modifier = modifier.padding(contentPadding)
    )
}

@Composable
@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun ReminderSearchPreview(
    @PreviewParameter(ReminderListProvider::class) reminders: ImmutableList<Reminder>
) {
    AppTheme {
        ReminderSearchScreen(
            uiState = ReminderSearchUiState(
                searchReminders = reminders,
                searchQuery = "Search query"
            ),
            onNavigateUp = {},
            onSearchQueryChange = {},
            onNavigateDetails = {},
            onCompleteReminder = {}
        )
    }
}
