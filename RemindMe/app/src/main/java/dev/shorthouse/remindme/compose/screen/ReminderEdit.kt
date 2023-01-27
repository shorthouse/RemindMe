package dev.shorthouse.remindme.compose.screen

import android.content.res.Configuration
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.preview.PreviewData
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.theme.RemindMeTheme
import dev.shorthouse.remindme.viewmodel.EditViewModel
import dev.shorthouse.remindme.viewmodel.InputViewModel

@Destination
@Composable
fun ReminderEditScreen(
    reminderId: Long,
    navigator: DestinationsNavigator
) {
    val inputViewModel: InputViewModel = hiltViewModel()
    val editViewModel: EditViewModel = hiltViewModel()

    val reminder by editViewModel.getReminder(reminderId).observeAsState()

    reminder?.let {
        ReminderInputScreen(
            reminderState = ReminderState(it),
            inputViewModel = inputViewModel,
            topBarTitle = stringResource(R.string.top_bar_title_edit_reminder),
            navigator = navigator
        )
    }
}

@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ReminderEditPreview() {
    RemindMeTheme {
        val reminderState by remember { mutableStateOf(PreviewData.reminderState) }
        val scaffoldState = rememberScaffoldState()

        ReminderInputScaffold(
            reminderState = reminderState,
            scaffoldState = scaffoldState,
            topBarTitle = stringResource(R.string.top_bar_title_edit_reminder),
            onNavigateUp = {},
            onSave = {},
        )
    }
}
