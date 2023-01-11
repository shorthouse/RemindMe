package dev.shorthouse.remindme.compose.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.viewmodel.EditViewModel
import dev.shorthouse.remindme.viewmodel.InputViewModel

@Composable
fun ReminderEditScreen(
    inputViewModel: InputViewModel = hiltViewModel(),
    editViewModel: EditViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit
) {
    val reminder by editViewModel.reminder.observeAsState()

    reminder?.let {
        val reminderState by remember { mutableStateOf(ReminderState(it)) }

        ReminderInputScreen(
            reminderState = reminderState,
            inputViewModel = inputViewModel,
            topBarTitle = stringResource(R.string.top_bar_title_edit_reminder),
            onNavigateUp = onNavigateUp,
        )
    }
}
