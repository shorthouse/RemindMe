package dev.shorthouse.remindme.ui.addedit.details

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.ui.addedit.ReminderAddEditContent
import dev.shorthouse.remindme.ui.addedit.ReminderAddEditEvent
import dev.shorthouse.remindme.ui.addedit.ReminderAddEditScreenNavArgs
import dev.shorthouse.remindme.ui.addedit.ReminderAddEditViewModel
import dev.shorthouse.remindme.ui.component.topappbar.RemindMeTopAppBar
import dev.shorthouse.remindme.ui.previewprovider.DefaultReminderProvider
import dev.shorthouse.remindme.ui.theme.AppTheme

@Destination(navArgsDelegate = ReminderAddEditScreenNavArgs::class)
@Composable
fun ReminderDetailsScreen(
    viewModel: ReminderAddEditViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (!uiState.isLoading) {
        ReminderDetailsScaffold(
            reminder = uiState.reminder,
            onHandleEvent = { viewModel.handleEvent(it) },
            onNavigateUp = { navigator.navigateUp() },
            isReminderValid = viewModel.isReminderValid(uiState.reminder)
        )
    }
}

@Composable
fun ReminderDetailsScaffold(
    reminder: Reminder,
    onHandleEvent: (ReminderAddEditEvent) -> Unit,
    onNavigateUp: () -> Unit,
    isReminderValid: Boolean,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            ReminderDetailsTopBar(
                reminder = reminder,
                onHandleEvent = onHandleEvent,
                onNavigateUp = onNavigateUp
            )
        },
        content = { scaffoldPadding ->
            ReminderAddEditContent(
                reminder = reminder,
                onHandleEvent = onHandleEvent,
                onNavigateUp = onNavigateUp,
                isReminderValid = isReminderValid,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding)
            )
        },
        modifier = modifier
    )
}

@Composable
fun ReminderDetailsTopBar(
    reminder: Reminder,
    onHandleEvent: (ReminderAddEditEvent) -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showOverflowMenu by remember { mutableStateOf(false) }

    RemindMeTopAppBar(
        title = stringResource(R.string.top_bar_title_reminder_details),
        navigationIcon = {
            IconButton(onClick = { onNavigateUp() }) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.cd_top_app_bar_back)
                )
            }
        },
        actions = {
            IconButton(onClick = { showOverflowMenu = !showOverflowMenu }) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = stringResource(R.string.cd_more)
                )
            }
            DropdownMenu(
                expanded = showOverflowMenu,
                onDismissRequest = { showOverflowMenu = false },
                offset = DpOffset(
                    x = 0.dp,
                    y = (-60).dp
                )
            ) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(R.string.dropdown_delete_reminder),
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 17.sp)
                        )
                    },
                    onClick = {
                        onHandleEvent(ReminderAddEditEvent.DeleteReminder(reminder))
                        onNavigateUp()
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(R.string.dropdown_complete_reminder),
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 17.sp)
                        )
                    },
                    onClick = {
                        onHandleEvent(ReminderAddEditEvent.CompleteReminder(reminder))
                        onNavigateUp()
                    }
                )
            }
        },
        modifier = modifier
    )
}

@Composable
@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun ReminderDetailsPreview(
    @PreviewParameter(DefaultReminderProvider::class) reminder: Reminder
) {
    AppTheme {
        ReminderDetailsScaffold(
            reminder = Reminder(),
            onHandleEvent = {},
            onNavigateUp = {},
            isReminderValid = true
        )
    }
}
