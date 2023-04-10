package dev.shorthouse.remindme.ui.input

import android.content.res.Configuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.ui.previewprovider.DefaultReminderStateProvider
import dev.shorthouse.remindme.ui.state.ReminderState
import dev.shorthouse.remindme.ui.theme.AppTheme

@Destination
@Composable
fun ReminderEditScreen(
    viewModel: ReminderInputViewModel = hiltViewModel(),
    reminderId: Long,
    navigator: DestinationsNavigator
) {
    viewModel.setReminder(reminderId = reminderId)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (!uiState.isLoading) {
        ReminderInputScreen(
            reminderState = ReminderState(uiState.reminder),
            viewModel = viewModel,
            topBarTitle = stringResource(R.string.top_bar_title_edit_reminder),
            navigator = navigator
        )
    }
}

@Composable
@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun ReminderEditPreview(
    @PreviewParameter(DefaultReminderStateProvider::class) reminderState: ReminderState
) {
    AppTheme {
        val snackbarHostState = remember { SnackbarHostState() }
        val topBarTitle = stringResource(R.string.top_bar_title_edit_reminder)

        ReminderInputScaffold(
            reminderState = reminderState,
            snackbarHostState = snackbarHostState,
            topBarTitle = topBarTitle,
            onNavigateUp = {},
            onSave = {}
        )
    }
}
