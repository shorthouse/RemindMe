package dev.shorthouse.remindme.ui.screen.input

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.RadioButton
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Notes
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.ui.component.dialog.DatePickerDialog
import dev.shorthouse.remindme.ui.component.dialog.TimePickerDialog
import dev.shorthouse.remindme.ui.component.text.RemindMeTextField
import dev.shorthouse.remindme.ui.preview.DefaultReminderProvider
import dev.shorthouse.remindme.ui.state.ReminderState
import dev.shorthouse.remindme.ui.theme.m2.RemindMeTheme
import dev.shorthouse.remindme.ui.theme.m2.SubtitleGrey
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ReminderInputScreen(
    reminderState: ReminderState,
    inputViewModel: InputViewModel,
    topBarTitle: String,
    navigator: DestinationsNavigator
) {
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val onNavigateUp: () -> Unit = {
        navigator.navigateUp()
    }

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
    onSave: () -> Unit
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            ReminderInputTopBar(
                topBarTitle = topBarTitle,
                onSave = onSave,
                onNavigateUp = onNavigateUp
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ReminderInputTopBar(
    topBarTitle: String,
    onNavigateUp: () -> Unit,
    onSave: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    TopAppBar(
        title = {
            Text(
                text = topBarTitle,
                style = MaterialTheme.typography.h6
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                keyboardController?.hide()
                onNavigateUp()
            }) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = stringResource(R.string.cd_top_bar_close_reminder)
                )
            }
        },
        actions = {
            IconButton(onClick = {
                keyboardController?.hide()
                onSave()
            }) {
                Icon(
                    imageVector = Icons.Rounded.Done,
                    contentDescription = stringResource(R.string.cd_top_bar_save_reminder),
                    tint = MaterialTheme.colors.onPrimary
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
    val surfaceColor = if (isSystemInDarkTheme()) {
        MaterialTheme.colors.background
    } else {
        MaterialTheme.colors.surface
    }

    Surface(color = surfaceColor) {
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
    }

    if (reminderState.id == 0L) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}

@Composable
fun ReminderNameInput(
    reminderState: ReminderState,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    val nameMaxLength = integerResource(R.integer.reminder_name_max_length)

    RemindMeTextField(
        text = reminderState.name,
        onTextChange = { if (it.length <= nameMaxLength) reminderState.name = it },
        textStyle = MaterialTheme.typography.h6.copy(color = MaterialTheme.colors.onSurface),
        hintText = stringResource(R.string.hint_reminder_name),
        imeAction = ImeAction.Done,
        modifier = modifier
            .padding(top = dimensionResource(R.dimen.margin_small))
            .focusRequester(focusRequester)
    )
}

@Composable
fun ReminderDateInput(reminderState: ReminderState, modifier: Modifier = Modifier) {
    val dateDialogState = rememberMaterialDialogState()

    DatePickerDialog(
        date = LocalDate.parse(
            reminderState.date,
            DateTimeFormatter.ofPattern("EEE, dd MMM yyyy")
        ),
        onDateChange = {
            reminderState.date = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy").format(it)
        },
        dialogState = dateDialogState
    )

    TextWithLeftIcon(
        icon = Icons.Rounded.CalendarToday,
        text = reminderState.date,
        modifier = modifier.clickable { dateDialogState.show() },
        contentDescription = stringResource(R.string.cd_details_date)
    )
}

@Composable
fun ReminderTimeInput(reminderState: ReminderState, modifier: Modifier = Modifier) {
    val timeDialogState = rememberMaterialDialogState()

    TimePickerDialog(
        time = reminderState.time,
        onTimeChange = { reminderState.time = it },
        dialogState = timeDialogState
    )

    TextWithLeftIcon(
        icon = Icons.Rounded.Schedule,
        text = reminderState.time.toString(),
        modifier = modifier.clickable { timeDialogState.show() },
        contentDescription = stringResource(R.string.cd_details_time)
    )
}

@Composable
fun ReminderSwitchRow(
    icon: ImageVector,
    iconContentDescription: String,
    switchText: String,
    switchTestTag: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        TextWithLeftIcon(
            icon = icon,
            text = switchText,
            contentDescription = iconContentDescription
        )

        Spacer(modifier = Modifier.weight(1f))

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(switchTestTag)
        )
    }
}

@Composable
fun ReminderNotificationInput(reminderState: ReminderState) {
    ReminderSwitchRow(
        icon = Icons.Rounded.NotificationsNone,
        iconContentDescription = stringResource(R.string.cd_details_notification),
        switchText = stringResource(R.string.title_send_notification),
        switchTestTag = stringResource(R.string.test_tag_switch_notification),
        isChecked = reminderState.isNotificationSent,
        onCheckedChange = { reminderState.isNotificationSent = it }
    )
}

@Composable
fun ReminderNotesInput(reminderState: ReminderState, modifier: Modifier = Modifier) {
    val notesMaxLength = integerResource(R.integer.reminder_notes_max_length)

    Row(modifier = modifier) {
        Icon(
            imageVector = Icons.Rounded.Notes,
            contentDescription = stringResource(R.string.cd_details_notes),
            tint = SubtitleGrey
        )

        Spacer(Modifier.width(dimensionResource(R.dimen.margin_normal)))

        RemindMeTextField(
            text = reminderState.notes.orEmpty(),
            onTextChange = { if (it.length <= notesMaxLength) reminderState.notes = it },
            textStyle = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onSurface),
            hintText = stringResource(R.string.hint_reminder_notes),
            imeAction = ImeAction.None,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ReminderRepeatIntervalInput(reminderState: ReminderState) {
    ReminderSwitchRow(
        icon = Icons.Rounded.Refresh,
        iconContentDescription = stringResource(R.string.cd_details_repeat_interval),
        switchText = stringResource(R.string.title_repeat_reminder),
        switchTestTag = stringResource(R.string.test_tag_switch_repeat_interval),
        isChecked = reminderState.isRepeatReminder,
        onCheckedChange = { reminderState.isRepeatReminder = it }
    )

    val repeatAmount = reminderState.repeatAmount.toIntOrNull() ?: 0
    reminderState.repeatUnit = when {
        stringResource(R.string.day) in reminderState.repeatUnit -> pluralStringResource(
            R.plurals.radio_button_days,
            repeatAmount
        )
        else -> pluralStringResource(
            R.plurals.radio_button_weeks,
            repeatAmount
        )
    }

    if (reminderState.isRepeatReminder) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            RepeatIntervalHeader()

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.End
                ) {
                    RepeatAmountInput(reminderState = reminderState)
                }

                Spacer(Modifier.width(dimensionResource(R.dimen.margin_large)))

                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.Start
                ) {
                    RepeatUnitInput(reminderState = reminderState)
                }
            }
        }
    }
}

@Composable
private fun RepeatIntervalHeader(modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.repeats_every_header),
            style = MaterialTheme.typography.subtitle1.copy(color = SubtitleGrey)
        )
    }
}

@Composable
private fun RepeatAmountInput(reminderState: ReminderState) {
    val repeatAmountMaxLength = integerResource(R.integer.reminder_repeat_amount_max_length)

    OutlinedTextField(
        value = reminderState.repeatAmount,
        onValueChange = {
            if (it.length <= repeatAmountMaxLength) {
                reminderState.repeatAmount = sanitiseRepeatAmount(
                    it
                )
            }
        },
        textStyle = MaterialTheme.typography.body1.copy(textAlign = TextAlign.Center),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Number
        ),
        modifier = Modifier
            .width(72.dp)
            .padding(end = dimensionResource(R.dimen.margin_normal))
            .testTag(stringResource(R.string.test_tag_text_field_repeat_amount))
    )
}

private fun sanitiseRepeatAmount(repeatAmount: String): String {
    return repeatAmount
        .trimStart { it == '0' }
        .filter { it.isDigit() }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun RepeatUnitInput(reminderState: ReminderState) {
    val repeatUnitPluralIds = listOf(R.plurals.radio_button_days, R.plurals.radio_button_weeks)
    val repeatAmount = reminderState.repeatAmount.toIntOrNull() ?: 0

    val repeatUnitOptions = repeatUnitPluralIds.map { pluralId ->
        pluralStringResource(
            pluralId,
            repeatAmount
        )
    }

    Column {
        repeatUnitOptions.forEach { text ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .selectable(
                        selected = (text == reminderState.repeatUnit),
                        onClick = { reminderState.repeatUnit = text }
                    )
                    .fillMaxWidth(0.8f)
            ) {
                RadioButton(
                    selected = (text == reminderState.repeatUnit),
                    onClick = { reminderState.repeatUnit = text },
                    modifier = Modifier.testTag(text)
                )

                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.margin_tiny)))

                Text(
                    text = text,
                    style = MaterialTheme.typography.body1
                )
            }
        }
    }
}

@Composable
fun TextWithLeftIcon(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = SubtitleGrey
        )

        Spacer(Modifier.width(dimensionResource(R.dimen.margin_normal)))

        Text(
            text = text,
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onBackground
        )
    }
}

@Composable
@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
private fun ReminderInputPreview(
    @PreviewParameter(DefaultReminderProvider::class) reminderState: ReminderState
) {
    RemindMeTheme {
        val scaffoldState = rememberScaffoldState()
        val topBarTitle = stringResource(R.string.top_bar_title_input_reminder)

        ReminderInputScaffold(
            reminderState = reminderState,
            scaffoldState = scaffoldState,
            topBarTitle = topBarTitle,
            onNavigateUp = {},
            onSave = {}
        )
    }
}
