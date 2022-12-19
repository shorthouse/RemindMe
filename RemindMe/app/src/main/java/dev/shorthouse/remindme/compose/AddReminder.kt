package dev.shorthouse.remindme.compose

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.google.android.material.composethemeadapter.MdcTheme
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.state.ReminderInputState

@Composable
fun AddReminderScreen(
    //addViewModel: AddViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit,
) {
    val reminderState by remember { mutableStateOf(ReminderInputState()) }

    val onSave = {
        //addViewModel.addReminder(inputReminder.toReminder())
        onNavigateUp()
    }

    AddReminderScaffold(
        reminderState = reminderState,
        onNavigateUp = onNavigateUp,
        onSave = onSave,
    )
}

@Composable
fun AddReminderScaffold(
    reminderState: ReminderInputState,
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
                innerPadding = innerPadding,
                reminderState = reminderState,
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
    innerPadding: PaddingValues,
    reminderState: ReminderInputState,
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

        val maxNameLength = 200
        val onNameChange = { name: String ->
            if (name.length <= maxNameLength) reminderState.reminderName = name
        }

        ReminderNameInput(
            reminderState,
            onNameChange = onNameChange
        )

        AddReminderSwitchRow(
            isChecked = reminderState.isNotificationSent,
            onCheckedChange = { reminderState.isNotificationSent = it },
            iconId = R.drawable.ic_notification_outline,
            iconContentDescriptionId = R.string.cd_icon_notification,
            switchStringId = R.string.title_send_notification
        )

        AddReminderSwitchRow(
            isChecked = reminderState.isRepeatReminder,
            onCheckedChange = { reminderState.isRepeatReminder = it },
            iconId = R.drawable.ic_repeat,
            iconContentDescriptionId = R.string.cd_icon_repeat,
            switchStringId = R.string.title_repeat_reminder
        )

        if (reminderState.isRepeatReminder) {
            // TODO
            // Show text boxt
            // Show radio buttons
        }
    }
}

@Composable
fun ReminderNameInput(
    reminderState: ReminderInputState,
    onNameChange: (String) -> Unit,
) {
    val textStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)

    BasicTextField(
        value = reminderState.reminderName,
        onValueChange = onNameChange,
        textStyle = textStyle,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            autoCorrect = true
        ),
        decorationBox = { innerTextField ->
            if (reminderState.reminderName.isEmpty()) {
                Text(
                    text = stringResource(R.string.hint_reminder_name),
                    fontSize = textStyle.fontSize,
                    fontWeight = textStyle.fontWeight,
                    color = colorResource(R.color.subtitle_grey)
                )
            }
            innerTextField()
        },
        modifier = Modifier
            .padding(top = dimensionResource(R.dimen.margin_large))
            .fillMaxWidth()
    )
}

@Composable
fun AddReminderSwitchRow(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    @DrawableRes iconId: Int,
    @StringRes iconContentDescriptionId: Int,
    @StringRes switchStringId: Int,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(iconId),
            contentDescription = stringResource(iconContentDescriptionId),
            tint = colorResource(R.color.icon_grey),
        )

        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.margin_normal)))

        Text(
            text = stringResource(switchStringId),
            fontSize = 18.sp,
        )

        Spacer(modifier = Modifier.weight(1f))

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Preview
@Composable
private fun AddReminderContentPreview() {
    MdcTheme {
        val reminderState by remember { mutableStateOf(ReminderInputState()) }

        AddReminderScaffold(
            reminderState = reminderState,
            onNavigateUp = {},
            onSave = {}
        )
    }
}
