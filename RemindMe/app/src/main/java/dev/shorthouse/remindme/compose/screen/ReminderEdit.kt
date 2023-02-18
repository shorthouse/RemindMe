package dev.shorthouse.remindme.compose.screen

import android.content.res.Configuration
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.previewdata.DefaultReminderProvider
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.theme.RemindMeTheme
import dev.shorthouse.remindme.viewmodel.InputViewModel

@OptIn(ExperimentalLifecycleComposeApi::class)
@Destination
@Composable
fun ReminderEditScreen(
    inputViewModel: InputViewModel = hiltViewModel(),
    reminderId: Long,
    navigator: DestinationsNavigator
) {
    val uiState by inputViewModel.uiState.collectAsStateWithLifecycle()
    inputViewModel.setReminderState(reminderId = reminderId)

    ReminderInputScreen(
        reminderState = uiState.reminderState,
        inputViewModel = inputViewModel,
        topBarTitle = stringResource(R.string.top_bar_title_edit_reminder),
        navigator = navigator
    )
}

@Composable
@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun ReminderEditPreview(
    @PreviewParameter(DefaultReminderProvider::class) reminderState: ReminderState
) {
    RemindMeTheme {
        val scaffoldState = rememberScaffoldState()
        val topBarTitle = stringResource(R.string.top_bar_title_edit_reminder)

        ReminderInputScaffold(
            reminderState = reminderState,
            scaffoldState = scaffoldState,
            topBarTitle = topBarTitle,
            onNavigateUp = {},
            onSave = {},
        )
    }
}
