package dev.shorthouse.remindme.compose.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import dev.shorthouse.remindme.compose.component.ReminderInputScreen
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.viewmodel.EditViewModel
import dev.shorthouse.remindme.viewmodel.InputViewModel

@Composable
fun ReminderEditScreen(
    inputViewModel: InputViewModel = hiltViewModel(),
    editViewModel: EditViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit
) {
    val reminderState by remember { mutableStateOf(ReminderState()) }

    ReminderInputScreen(
        reminderState = reminderState,
        inputViewModel = inputViewModel,
        saveReminder = { editViewModel.editReminder(reminderState.toReminder()) },
        onNavigateUp = onNavigateUp,
    )
}
