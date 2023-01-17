package dev.shorthouse.remindme.compose.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.viewmodel.EditViewModel
import dev.shorthouse.remindme.viewmodel.InputViewModel

@Composable
fun ReminderEditScreen(
    reminderId: Long?,
    inputViewModel: InputViewModel = hiltViewModel(),
    editViewModel: EditViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit
) {
    val reminder = reminderId?.let { editViewModel.getReminder(it).observeAsState() }

    reminder?.value?.let {
        ReminderInputScreen(
            reminderState = ReminderState(it),
            inputViewModel = inputViewModel,
            topBarTitle = stringResource(R.string.top_bar_title_edit_reminder),
            onNavigateUp = onNavigateUp,
        )
    }
}
