package dev.shorthouse.remindme.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import dev.shorthouse.remindme.model.InputReminder
import dev.shorthouse.remindme.model.InputRepeatInterval

@Composable
fun AddReminderScreen(
    //addViewModel: AddViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit,
) {
    //var inputReminder by remember { mutableStateOf(addViewModel.addReminderInitialValues) }

    var inputReminder by remember {
        mutableStateOf(
            InputReminder(
                name = "",
                startDate = "Wed, 22 Mar 2000",
                startTime = "15:30",
                isNotificationSent = false,
                repeatInterval = InputRepeatInterval(
                    R.plurals.interval_days,
                    1
                ),
                notes = null,
                isComplete = false
            )
        )
    }
    val maxNameLength = 200

    val onSave = {
        //addViewModel.addReminder(inputReminder.toReminder())
        onNavigateUp()
    }

    val onNameChange = { name: String ->
        if (name.length <= maxNameLength) inputReminder = inputReminder.copy(name = name)
    }

    val onNotificationChange = { isNotificationSent: Boolean ->
        inputReminder = inputReminder.copy(isNotificationSent = isNotificationSent)
    }

    AddReminderScaffold(
        reminder = inputReminder,
        onNavigateUp = onNavigateUp,
        onSave = onSave,
        onNameChange = onNameChange,
        onNotificationChange = onNotificationChange
    )
}

@Composable
fun AddReminderScaffold(
    reminder: InputReminder,
    onSave: () -> Unit,
    onNavigateUp: () -> Unit,
    onNameChange: (String) -> Unit,
    onNotificationChange: (Boolean) -> Unit,
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
                reminder = reminder,
                onNameChange = onNameChange,
                onNotificationChange = onNotificationChange
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
    reminder: InputReminder,
    onNameChange: (String) -> Unit,
    onNotificationChange: (Boolean) -> Unit,
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
        ReminderNameInput(
            reminder,
            onNameChange = onNameChange
        )

        AddReminderNotification(
            reminder.isNotificationSent,
            onNotificationChange
        )

//        val icons = listOf(
//            R.drawable.ic_calendar,
//            R.drawable.ic_clock,
//            R.drawable.ic_notification_outline,
//            R.drawable.ic_repeat,
//        )
//
//        val iconContentDescriptions = listOf(
//            R.string.cd_icon_calendar,
//            R.string.cd_icon_clock,
//            R.string.cd_icon_notification,
//            R.string.cd_icon_repeat
//        )
//
//        val titleTexts = listOf(
//            reminder.startDate,
//            reminder.startTime,
//            stringResource(R.string.title_send_notification),
//            stringResource(R.string.title_repeat_reminder),
//        )
//
//        for (i in icons.indices) {
//            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_large)))
//
//            AddReminderRow(
//                iconId = icons[i],
//                iconContentDescriptionId = iconContentDescriptions[i],
//                titleText = titleTexts[i]
//            )
//        }

//        AddReminderRow(
//            reminder.startDate,
//            R.drawable.ic_calendar,
//            R.string.cd_icon_calendar
//        )
//
//        AddReminderRow(
//            reminder.startTime,
//            R.drawable.ic_clock,
//            R.string.cd_icon_clock
//        )
//
//        AddReminderRow(
//            stringResource(R.string.title_send_notification),
//            R.drawable.ic_notification_outline,
//            R.string.cd_icon_notification
//        )
//
//        AddReminderRow(
//            stringResource(R.string.title_repeat_reminder),
//            R.drawable.ic_repeat,
//            R.string.cd_icon_repeat
//        )
    }
}

@Composable
fun ReminderNameInput(
    reminder: InputReminder,
    onNameChange: (String) -> Unit,
) {
    val textStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)

    BasicTextField(
        value = reminder.name,
        onValueChange = onNameChange,
        textStyle = textStyle,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            autoCorrect = true
        ),
        decorationBox = { innerTextField ->
            if (reminder.name.isEmpty()) {
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
fun AddReminderNotification(
    isNotificationSent: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_notification_outline),
            contentDescription = stringResource(R.string.cd_icon_notification),
            tint = colorResource(R.color.icon_grey),
        )

        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.margin_normal)))

        Text(
            text = stringResource(R.string.title_send_notification),
            fontSize = 18.sp,
        )

        Spacer(modifier = Modifier.weight(1f))

        Switch(
            checked = isNotificationSent,
            onCheckedChange = onCheckedChange
        )
    }
}

//@Composable
//fun AddReminderRow(
//    iconId: Int,
//    iconContentDescriptionId: Int,
//    titleText: String,
//    onValueChange:
//) {
//    Row(
//        verticalAlignment = Alignment.CenterVertically,
//    ) {
//        Icon(
//            painter = painterResource(iconId),
//            contentDescription = stringResource(iconContentDescriptionId),
//            tint = colorResource(R.color.icon_grey),
//        )
//
//        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.margin_normal)))
//
//        Text(
//            text = titleText,
//            fontSize = 18.sp,
//        )
//    }
//}

@Preview
@Composable
private fun AddReminderContentPreview() {
    MdcTheme {
        val previewReminder = InputReminder(
            name = "",
            startDate = "Wed, 22 Mar 2000",
            startTime = "15:30",
            isNotificationSent = false,
            repeatInterval = InputRepeatInterval(
                R.plurals.interval_days,
                1
            ),
            notes = null,
            isComplete = false
        )

        AddReminderScreen(
            onNavigateUp = {}
        )
    }
}
