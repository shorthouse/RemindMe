package dev.shorthouse.remindme.compose.screen

import android.content.res.Configuration
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.previewdata.EmptyReminderProvider
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.theme.RemindMeTheme
import dev.shorthouse.remindme.viewmodel.InputViewModel

@Destination
@Composable
fun ReminderAddScreen(
    navigator: DestinationsNavigator
) {
    val inputViewModel: InputViewModel = hiltViewModel()

    val reminderState by remember { mutableStateOf(ReminderState()) }

    ReminderInputScreen(
        reminderState = reminderState,
        inputViewModel = inputViewModel,
        topBarTitle = stringResource(R.string.top_bar_title_add_reminder),
        navigator = navigator
    )
}

@Composable
@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
private fun ReminderAddPreview(
    @PreviewParameter(EmptyReminderProvider::class) reminderState: ReminderState
) {
    RemindMeTheme {
        val scaffoldState = rememberScaffoldState()
        val topBarTitle = stringResource(R.string.top_bar_title_add_reminder)

        ReminderInputScaffold(
            reminderState = reminderState,
            scaffoldState = scaffoldState,
            topBarTitle = topBarTitle,
            onNavigateUp = {},
            onSave = {}
        )
    }
}
