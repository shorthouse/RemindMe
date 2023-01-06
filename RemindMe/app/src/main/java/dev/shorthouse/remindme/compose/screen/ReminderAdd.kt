package dev.shorthouse.remindme.compose.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import dev.shorthouse.remindme.compose.component.ReminderInputContent
import dev.shorthouse.remindme.compose.component.ReminderInputScaffold
import dev.shorthouse.remindme.compose.component.ReminderInputTopBar
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.viewmodel.AddViewModel
import dev.shorthouse.remindme.viewmodel.InputViewModel
import kotlinx.coroutines.launch

@Composable
fun ReminderAddScreen(
    inputViewModel: InputViewModel = hiltViewModel(),
    addViewModel: AddViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit
) {
    val reminderState by remember { mutableStateOf(ReminderState()) }

    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val onSave: () -> Unit = {
        val reminder = reminderState.toReminder()

        when {
            inputViewModel.isReminderValid(reminder) -> {
                addViewModel.addReminder(reminder)
                onNavigateUp()
            }
            else -> {
                val errorMessage = inputViewModel.getErrorMessage(reminder).asString(context)
                coroutineScope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(message = errorMessage)
                }
            }
        }
    }

    // Set title here? or just have an addscaffold and an editscaffold
    ReminderInputScaffold(
        reminderState = reminderState,
        scaffoldState = scaffoldState,
        onNavigateUp = onNavigateUp,
        onSave = onSave
    )
}

@Composable
private fun ReminderAddScaffold(
    reminderState: ReminderState,
    scaffoldState: ScaffoldState,
    onNavigateUp: () -> Unit,
    onSave: () -> Unit,
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            ReminderInputTopBar(
                onNavigateUp = onNavigateUp,
                onSave = onSave
            )
        },
        content = { scaffoldPadding ->
            ReminderInputContent(
                reminderState = reminderState,
                modifier = Modifier
                    .padding(scaffoldPadding)
                    .fillMaxSize()
            )
        }
    )
}
