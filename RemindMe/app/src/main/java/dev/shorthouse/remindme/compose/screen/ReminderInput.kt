package dev.shorthouse.remindme.compose.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.material.composethemeadapter.MdcTheme
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.component.*
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.viewmodel.InputViewModel
import kotlinx.coroutines.launch

@Composable
fun ReminderInputScreen(
    reminderState: ReminderState,
    inputViewModel: InputViewModel,
    topBarTitle: String,
    onNavigateUp: () -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val onSave: () -> Unit = {
        val reminder = reminderState.toReminder()

        when {
            inputViewModel.isReminderValid(reminder) -> {
                inputViewModel.saveReminder(reminder)
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

    ReminderInputScaffold(
        reminderState = reminderState,
        scaffoldState = scaffoldState,
        topBarTitle = topBarTitle,
        onNavigateUp = onNavigateUp,
        onSave = onSave
    )
}

@Composable
fun ReminderInputScaffold(
    reminderState: ReminderState,
    scaffoldState: ScaffoldState,
    topBarTitle: String,
    onNavigateUp: () -> Unit,
    onSave: () -> Unit,
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            ReminderInputTopBar(
                topBarTitle = topBarTitle,
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

@Composable
fun ReminderInputTopBar(
    topBarTitle: String,
    onNavigateUp: () -> Unit,
    onSave: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(text = topBarTitle)
        },
        navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = stringResource(R.string.cd_top_bar_close_reminder),
                    tint = colorResource(R.color.on_primary)
                )
            }
        },
        actions = {
            IconButton(onClick = onSave) {
                Icon(
                    painter = painterResource(R.drawable.ic_tick),
                    contentDescription = stringResource(R.string.cd_top_bar_save_reminder),
                    tint = colorResource(R.color.on_primary)
                )
            }
        }
    )
}

@Composable
fun ReminderInputContent(
    reminderState: ReminderState,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = dimensionResource(R.dimen.margin_normal))
    ) {
        val spacingModifier = Modifier
            .padding(vertical = dimensionResource(R.dimen.margin_small))
            .fillMaxWidth()

        ReminderNameInput(
            reminderState = reminderState,
            focusRequester = focusRequester,
            modifier = spacingModifier
        )

        ReminderDateInput(
            reminderState = reminderState,
            modifier = spacingModifier
        )

        ReminderTimeInput(
            reminderState = reminderState,
            modifier = spacingModifier
        )

        ReminderNotificationInput(
            reminderState = reminderState
        )

        ReminderRepeatIntervalInput(
            reminderState = reminderState
        )

        ReminderNotesInput(
            reminderState = reminderState,
            modifier = spacingModifier
        )
    }

    if (reminderState.id == 0L) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}

@Preview
@Composable
private fun ReminderAddPreview() {
    MdcTheme {
        val reminderState by remember { mutableStateOf(ReminderState()) }
        val scaffoldState = rememberScaffoldState()

        ReminderInputScaffold(
            reminderState = reminderState,
            scaffoldState = scaffoldState,
            topBarTitle = stringResource(R.string.top_bar_title_add_reminder),
            onNavigateUp = {},
            onSave = {},
        )
    }
}

@Preview
@Composable
private fun ReminderEditPreview() {
    MdcTheme {
        val reminderState by remember { mutableStateOf(ReminderState()) }
        val scaffoldState = rememberScaffoldState()

        reminderState.name = "Yoga with Alice"
        reminderState.isRepeatReminder = true
        reminderState.isNotificationSent = true
        reminderState.notes = "Don't forget the yoga mat!"

        ReminderInputScaffold(
            reminderState = reminderState,
            scaffoldState = scaffoldState,
            topBarTitle = stringResource(R.string.top_bar_title_edit_reminder),
            onNavigateUp = {},
            onSave = {},
        )
    }
}
