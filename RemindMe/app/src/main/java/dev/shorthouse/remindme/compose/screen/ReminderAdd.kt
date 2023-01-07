package dev.shorthouse.remindme.compose.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.viewmodel.InputViewModel

@Composable
fun ReminderAddScreen(
    inputViewModel: InputViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit
) {
    val reminderState by remember { mutableStateOf(ReminderState()) }

    ReminderInputScreen(
        reminderState = reminderState,
        inputViewModel = inputViewModel,
        topBarTitle = stringResource(R.string.top_bar_title_edit_reminder),
        onNavigateUp = onNavigateUp,
    )
}
