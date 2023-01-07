package dev.shorthouse.remindme.compose.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.material.composethemeadapter.MdcTheme
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.state.ReminderState

@Composable
fun ReminderInputScaffold(
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


@Composable
fun ReminderInputTopBar(
    onNavigateUp: () -> Unit,
    onSave: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(text = stringResource(R.string.top_bar_title_add_reminder))
        },
        navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = stringResource(R.string.cd_top_bar_close_reminder)
                )
            }
        },
        actions = {
            IconButton(onClick = onSave) {
                Icon(
                    painter = painterResource(R.drawable.ic_tick),
                    contentDescription = stringResource(R.string.cd_top_bar_save_reminder),
                    tint = Color.White
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
        val paddingModifier = Modifier.padding(vertical = dimensionResource(R.dimen.margin_small))

        ReminderNameInput(
            reminderState = reminderState,
            focusRequester = focusRequester,
            modifier = paddingModifier
        )

        ReminderDateInput(
            reminderState = reminderState,
            modifier = paddingModifier
        )

        ReminderTimeInput(
            reminderState = reminderState,
            modifier = paddingModifier
        )

        ReminderNotificationInput(reminderState = reminderState)

        ReminderRepeatIntervalInput(reminderState = reminderState)

        ReminderNotesInput(
            reminderState = reminderState,
            modifier = paddingModifier
        )
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Preview
@Composable
private fun ReminderInputPreview() {
    MdcTheme {
        val reminderState by remember { mutableStateOf(ReminderState()) }
        val scaffoldState = rememberScaffoldState()

        reminderState.isRepeatReminder = true

        ReminderInputScaffold(reminderState, scaffoldState, {}, {})
    }
}
