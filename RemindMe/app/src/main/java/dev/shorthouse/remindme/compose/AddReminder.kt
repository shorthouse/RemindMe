package dev.shorthouse.remindme.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.material.composethemeadapter.MdcTheme
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.model.DisplayReminder
import dev.shorthouse.remindme.model.DisplayRepeatInterval
import dev.shorthouse.remindme.theme.White
import dev.shorthouse.remindme.viewmodel.AddEditViewModel
import java.time.ZonedDateTime

@Composable
fun AddReminderScreen(
    addEditViewModel: AddEditViewModel = viewModel(),
    onNavigateUp: () -> Unit,
    onSave: () -> Unit,
) {
    // TODO put this into the fragment instead and pass the reminder in

    val addReminderDefaults = DisplayReminder(
        name = "",
        startDate = addEditViewModel.getFormattedDate(ZonedDateTime.now()),
        startTime = addEditViewModel.getFormattedTimeNextHour(ZonedDateTime.now()),
        isNotificationSent = false,
        repeatInterval = DisplayRepeatInterval(
            R.plurals.interval_days,
            1
        ),
        notes = null
    )

    AddReminderScaffold(
        reminder = addReminderDefaults,
        onSave = onSave,
        onNavigateUp = onNavigateUp
    )
}

@Composable
fun AddReminderScaffold(
    reminder: DisplayReminder,
    onSave: () -> Unit,
    onNavigateUp: () -> Unit,
) {
    Scaffold(
        topBar = {
            AddReminderTopBar(
                onNavigateUp = onNavigateUp,
                onSave = onSave
            )
        },
        content = { innerPadding ->
            AddReminderContent(
                reminder = reminder,
                innerPadding = innerPadding
            )
        }
    )
}

@Composable
fun AddReminderTopBar(
    onNavigateUp: () -> Unit,
    onSave: () -> Unit,
) {
    TopAppBar(
        modifier = Modifier.testTag("AddReminderTopBar"),
        title = {
            Text(
                text = stringResource(R.string.top_bar_title_add_reminder)
            )
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
                    contentDescription = stringResource(R.string.cd_top_bar_save_reminder)
                )
            }
        }
    )
}

@Composable
fun AddReminderContent(
    reminder: DisplayReminder,
    innerPadding: PaddingValues
) {
    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(
                start = dimensionResource(R.dimen.margin_normal),
                end = dimensionResource(R.dimen.margin_normal),
                top = innerPadding.calculateTopPadding(),
            )
    ) {
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_small)))

        ReminderNameInput(reminder)
    }
}

@Composable
fun ReminderNameInput(reminder: DisplayReminder) {
    var reminderName by remember { mutableStateOf(reminder.name) }
    val maxLength = 200
    val textStyle = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )

    OutlinedTextField(
        value = reminderName,
        placeholder = {
            Text(
                text = stringResource(R.string.hint_reminder_name),
                fontSize = textStyle.fontSize,
                fontWeight = textStyle.fontWeight
            )
        },
        singleLine = false,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = White,
            unfocusedBorderColor = White
        ),
        textStyle = textStyle,
        onValueChange = { if (it.length <= maxLength) reminderName = it },
        modifier = Modifier
            .fillMaxWidth()
    )
}

@Preview
@Composable
private fun AddReminderScreenPreview() {
    MdcTheme {
        val addReminderDefaults = DisplayReminder(
            name = "",
            startDate = "Wed, 22 Mar 2000",
            startTime = "14:30",
            isNotificationSent = false,
            repeatInterval = DisplayRepeatInterval(R.plurals.interval_days, 1),
            notes = null
        )

        AddReminderScaffold(
            reminder = addReminderDefaults,
            onNavigateUp = {},
            onSave = {}
        )
    }
}
