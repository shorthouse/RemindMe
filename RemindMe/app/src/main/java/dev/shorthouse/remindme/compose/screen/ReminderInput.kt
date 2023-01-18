package dev.shorthouse.remindme.compose.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.material.composethemeadapter.MdcTheme
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ReminderInputTopBar(
    topBarTitle: String,
    onNavigateUp: () -> Unit,
    onSave: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    TopAppBar(
        title = {
            Text(text = topBarTitle)
        },
        navigationIcon = {
            IconButton(onClick = {
                keyboardController?.hide()
                onNavigateUp()
            }) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = stringResource(R.string.cd_top_bar_close_reminder),
                    tint = colorResource(R.color.on_primary)
                )
            }
        },
        actions = {
            IconButton(onClick = {
                keyboardController?.hide()
                onSave()
            }) {
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

//    if (reminderState.id == 0L) {
//        LaunchedEffect(Unit) {
//            focusRequester.requestFocus()
//        }
//    }
}

@Composable
fun ReminderNameInput(reminderState: ReminderState, focusRequester: FocusRequester, modifier: Modifier = Modifier) {
    val nameMaxLength = integerResource(R.integer.reminder_name_max_length)

    ReminderTextField(
        text = reminderState.name,
        onTextChange = { if (it.length <= nameMaxLength) reminderState.name = it },
        textStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
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

    ReminderDatePickerDialog(
        reminderState = reminderState,
        dialogState = dateDialogState
    )

    TextWithLeftIcon(
        icon = Icons.Rounded.CalendarToday,
        text = reminderState.date,
        modifier = modifier
            .clickable { dateDialogState.show() }
    )
}

@Composable
fun ReminderTimeInput(reminderState: ReminderState, modifier: Modifier = Modifier) {
    val timeDialogState = rememberMaterialDialogState()

    ReminderTimePickerDialog(
        reminderState = reminderState,
        dialogState = timeDialogState,
    )

    TextWithLeftIcon(
        icon = Icons.Rounded.Schedule,
        text = reminderState.time.toString(),
        modifier = modifier
            .clickable { timeDialogState.show() }
    )
}

@Composable
fun ReminderNotificationInput(reminderState: ReminderState) {
    ReminderSwitchRow(
        icon = Icons.Rounded.NotificationsNone,
        switchText = stringResource(R.string.title_send_notification),
        isChecked = reminderState.isNotificationSent,
        onCheckedChange = { reminderState.isNotificationSent = it }
    )
}

@Composable
fun ReminderNotesInput(reminderState: ReminderState, modifier: Modifier = Modifier) {
    val notesMaxLength = integerResource(R.integer.reminder_notes_max_length)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_notes),
            contentDescription = null,
            tint = colorResource(R.color.icon_grey),
        )

        Spacer(Modifier.width(dimensionResource(R.dimen.margin_normal)))

        ReminderTextField(
            text = reminderState.notes.orEmpty(),
            onTextChange = { if (it.length <= notesMaxLength) reminderState.notes = it },
            textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Normal),
            hintText = stringResource(R.string.hint_reminder_notes),
            imeAction = ImeAction.None,
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ReminderRepeatIntervalInput(reminderState: ReminderState) {
    ReminderSwitchRow(
        icon = Icons.Rounded.Refresh,
        switchText = stringResource(R.string.title_repeat_reminder),
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
            color = colorResource(R.color.subtitle_grey)
        )
    }
}

@Composable
private fun RepeatAmountInput(reminderState: ReminderState) {
    val repeatAmountMaxLength = integerResource(R.integer.reminder_repeat_amount_max_length)

    OutlinedTextField(
        value = reminderState.repeatAmount,
        onValueChange = {
            if (it.length <= repeatAmountMaxLength) reminderState.repeatAmount = sanitiseRepeatAmount(it)
        },
        textStyle = TextStyle(textAlign = TextAlign.Center),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Number
        ),
        modifier = Modifier
            .width(72.dp)
            .padding(end = dimensionResource(R.dimen.margin_normal))
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

    val repeatUnitOptions = repeatUnitPluralIds.map { pluralId -> pluralStringResource(pluralId, repeatAmount) }

    Column {
        repeatUnitOptions.forEach { text ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .selectable(
                        selected = (text == reminderState.repeatUnit),
                        onClick = { reminderState.repeatUnit = text }
                    )
            ) {
                RadioButton(
                    selected = (text == reminderState.repeatUnit),
                    onClick = { reminderState.repeatUnit = text }
                )

                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.margin_tiny)))

                Text(text = text)
            }
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
