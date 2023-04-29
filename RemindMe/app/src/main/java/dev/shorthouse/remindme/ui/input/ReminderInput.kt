package dev.shorthouse.remindme.ui.input

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Notes
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.ui.component.dialog.ReminderTimePicker
import dev.shorthouse.remindme.ui.component.text.RemindMeTextField
import dev.shorthouse.remindme.ui.component.topappbar.RemindMeTopAppBar
import dev.shorthouse.remindme.ui.previewprovider.DefaultReminderStateProvider
import dev.shorthouse.remindme.ui.state.ReminderState
import dev.shorthouse.remindme.ui.theme.AppTheme
import kotlinx.coroutines.launch

@Composable
fun ReminderInputScreen(
    reminderState: ReminderState,
    viewModel: ReminderInputViewModel,
    topBarTitle: String,
    navigator: DestinationsNavigator
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val onNavigateUp: () -> Unit = {
        navigator.navigateUp()
    }

    val onSave: () -> Unit = {
        val reminder = reminderState.toReminder()

        when {
            viewModel.isReminderValid(reminder) -> {
                viewModel.saveReminder(reminder)
                onNavigateUp()
            }
            else -> {
                val errorMessage = viewModel.getErrorMessage(reminder).asString(context)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(message = errorMessage)
                }
            }
        }
    }

    ReminderInputScaffold(
        reminderState = reminderState,
        snackbarHostState = snackbarHostState,
        topBarTitle = topBarTitle,
        onNavigateUp = onNavigateUp,
        onSave = onSave
    )
}

@Composable
fun ReminderInputScaffold(
    reminderState: ReminderState,
    snackbarHostState: SnackbarHostState,
    topBarTitle: String,
    onNavigateUp: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
        },
        modifier = modifier
    )
}

@Composable
fun ReminderInputTopBar(
    topBarTitle: String,
    onNavigateUp: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    RemindMeTopAppBar(
        title = topBarTitle,
        navigationIcon = {
            IconButton(onClick = { onNavigateUp() }) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = stringResource(R.string.cd_top_bar_close_reminder)
                )
            }
        },
        actions = {
            IconButton(onClick = { onSave() }) {
                Icon(
                    imageVector = Icons.Rounded.Done,
                    contentDescription = stringResource(R.string.cd_top_bar_save_reminder)
                )
            }
        },
        modifier = modifier
    )
}

@Composable
fun ReminderInputContent(
    reminderState: ReminderState,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val surfaceColor = if (isSystemInDarkTheme()) {
        MaterialTheme.colorScheme.background
    } else {
        MaterialTheme.colorScheme.surface
    }

    Surface(color = surfaceColor) {
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            val spacingModifier = Modifier
                .padding(vertical = 12.dp)
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
    RemindMeTextField(
        text = reminderState.name,
        onTextChange = { if (it.length <= 200) reminderState.name = it },
        textStyle = MaterialTheme.typography.titleLarge
            .copy(color = MaterialTheme.colorScheme.onSurface),
        hintText = stringResource(R.string.hint_reminder_name),
        imeAction = ImeAction.Done,
        modifier = modifier
            .padding(top = 12.dp)
            .focusRequester(focusRequester)
    )
}

@Composable
fun ReminderDateInput(reminderState: ReminderState, modifier: Modifier = Modifier) {
    var isDatePickerShown by remember { mutableStateOf(false) }

//    if (isDatePickerShown) {
//        ReminderDatePicker(
//            initialDateOld = reminderState.date,
//            onConfirm = { reminderState.date = it },
//            onDismiss = { isDatePickerShown = false }
//        )
//    }

    TextWithLeftIcon(
        icon = Icons.Rounded.CalendarToday,
        text = reminderState.date,
        modifier = modifier.clickable { isDatePickerShown = true },
        contentDescription = stringResource(R.string.cd_details_date)
    )
}

@Composable
fun ReminderTimeInput(reminderState: ReminderState, modifier: Modifier = Modifier) {
    var isTimePickerShown by remember { mutableStateOf(false) }

    if (isTimePickerShown) {
        ReminderTimePicker(
            initialTime = reminderState.time,
            onConfirm = { reminderState.time = it },
            onDismiss = { isTimePickerShown = false }
        )
    }

    TextWithLeftIcon(
        icon = Icons.Rounded.Schedule,
        text = reminderState.time.toString(),
        modifier = modifier.clickable { isTimePickerShown = true },
        contentDescription = stringResource(R.string.cd_details_time)
    )
}

@Composable
fun ReminderSwitchRow(
    icon: ImageVector,
    iconContentDescription: String?,
    switchText: String,
    switchTestTag: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
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
fun ReminderNotificationInput(
    reminderState: ReminderState,
    modifier: Modifier = Modifier
) {
    ReminderSwitchRow(
        icon = Icons.Rounded.NotificationsNone,
        iconContentDescription = stringResource(R.string.cd_details_notification),
        switchText = stringResource(R.string.title_send_notification),
        switchTestTag = stringResource(R.string.test_tag_switch_notification),
        isChecked = reminderState.isNotificationSent,
        onCheckedChange = { reminderState.isNotificationSent = it },
        modifier = modifier
    )
}

@Composable
fun ReminderNotesInput(
    reminderState: ReminderState,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Icon(
            imageVector = Icons.Rounded.Notes,
            contentDescription = stringResource(R.string.cd_details_notes),
            tint = MaterialTheme.colorScheme.outline
        )

        Spacer(Modifier.width(16.dp))

        RemindMeTextField(
            text = reminderState.notes.orEmpty(),
            onTextChange = { if (it.length <= 2000) reminderState.notes = it },
            textStyle = MaterialTheme.typography.bodyMedium
                .copy(color = MaterialTheme.colorScheme.onSurface),
            hintText = stringResource(R.string.hint_reminder_notes),
            imeAction = ImeAction.None,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ReminderRepeatIntervalInput(
    reminderState: ReminderState,
    modifier: Modifier = Modifier
) {
    ReminderSwitchRow(
        icon = Icons.Rounded.Refresh,
        iconContentDescription = null,
        switchText = stringResource(R.string.title_repeat_reminder),
        switchTestTag = stringResource(R.string.test_tag_switch_repeat_interval),
        isChecked = reminderState.isRepeatReminder,
        onCheckedChange = { reminderState.isRepeatReminder = it },
        modifier = modifier
    )

    val repeatAmount = reminderState.repeatAmount.toIntOrNull() ?: 0

    reminderState.repeatUnit = when {
        stringResource(R.string.day) in reminderState.repeatUnit -> pluralStringResource(
            R.plurals.repeat_unit_days,
            repeatAmount
        )
        else -> pluralStringResource(
            R.plurals.repeat_unit_weeks,
            repeatAmount
        )
    }

    if (reminderState.isRepeatReminder) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.repeats_every_header),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.outline,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.End
                ) {
                    RepeatAmountInput(reminderState = reminderState)
                }

                Spacer(Modifier.width(24.dp))

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
private fun RepeatAmountInput(
    reminderState: ReminderState,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = reminderState.repeatAmount,
        onValueChange = { repeatAmount ->
            if (repeatAmount.length <= 2) {
                reminderState.repeatAmount = repeatAmount
                    .trimStart { it == '0' }
                    .filter { it.isDigit() }
            }
        },
        textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Number
        ),
        modifier = modifier
            .width(72.dp)
            .padding(end = 16.dp)
            .testTag(stringResource(R.string.test_tag_text_field_repeat_amount))
    )
}

@Composable
private fun RepeatUnitInput(
    reminderState: ReminderState,
    modifier: Modifier = Modifier
) {
    val repeatUnitPluralIds = listOf(R.plurals.repeat_unit_days, R.plurals.repeat_unit_weeks)
    val repeatAmount = reminderState.repeatAmount.toIntOrNull() ?: 0

    val repeatUnitOptions = repeatUnitPluralIds.map { pluralId ->
        pluralStringResource(
            pluralId,
            repeatAmount
        )
    }

    Column(modifier = modifier) {
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

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun TextWithLeftIcon(
    icon: ImageVector,
    text: String,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.outline
        )

        Spacer(Modifier.width(16.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun ReminderInputPreview(
    @PreviewParameter(DefaultReminderStateProvider::class) reminderState: ReminderState
) {
    AppTheme {
        val snackbarHostState = remember { SnackbarHostState() }
        val topBarTitle = stringResource(R.string.top_bar_title_input_reminder)

        ReminderInputScaffold(
            reminderState = reminderState,
            snackbarHostState = snackbarHostState,
            topBarTitle = topBarTitle,
            onNavigateUp = {},
            onSave = {}
        )
    }
}
