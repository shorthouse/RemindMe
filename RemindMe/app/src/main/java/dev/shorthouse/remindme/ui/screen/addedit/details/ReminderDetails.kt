package dev.shorthouse.remindme.ui.screen.addedit.details

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.ui.component.progressindicator.CenteredCircularProgressIndicator
import dev.shorthouse.remindme.ui.component.snackbar.RemindMeSnackbar
import dev.shorthouse.remindme.ui.component.topappbar.RemindMeTopAppBar
import dev.shorthouse.remindme.ui.previewprovider.DefaultReminderProvider
import dev.shorthouse.remindme.ui.screen.addedit.ReminderAddEditContent
import dev.shorthouse.remindme.ui.screen.addedit.ReminderAddEditEvent
import dev.shorthouse.remindme.ui.screen.addedit.ReminderAddEditScreenNavArgs
import dev.shorthouse.remindme.ui.screen.addedit.ReminderAddEditUiState
import dev.shorthouse.remindme.ui.screen.addedit.ReminderAddEditViewModel
import dev.shorthouse.remindme.ui.theme.AppTheme

@Composable
@Destination(navArgsDelegate = ReminderAddEditScreenNavArgs::class)
fun ReminderDetailsScreen(
    navigator: DestinationsNavigator,
    viewModel: ReminderAddEditViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ReminderDetailsScreen(
        uiState = uiState,
        onHandleEvent = { viewModel.handleEvent(it) },
        onNavigateUp = { navigator.navigateUp() }
    )
}

@Composable
fun ReminderDetailsScreen(
    uiState: ReminderAddEditUiState,
    onHandleEvent: (ReminderAddEditEvent) -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { snackbarData ->
                RemindMeSnackbar(snackbarData = snackbarData)
            }
        },
        topBar = {
            ReminderDetailsTopBar(
                reminder = uiState.reminder,
                onHandleEvent = onHandleEvent,
                onNavigateUp = onNavigateUp
            )
        },
        content = { scaffoldPadding ->
            if (uiState.isLoading) {
                CenteredCircularProgressIndicator()
            } else {
                ReminderAddEditContent(
                    reminder = uiState.reminder,
                    isReminderValid = uiState.isReminderValid,
                    onHandleEvent = onHandleEvent,
                    onNavigateUp = onNavigateUp,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(scaffoldPadding)
                )
            }
        },
        modifier = modifier
    )

    uiState.snackbarMessage?.let { message ->
        val snackbarText = stringResource(message.messageId)

        LaunchedEffect(snackbarHostState, snackbarText) {
            snackbarHostState.showSnackbar(
                message = snackbarText,
                duration = SnackbarDuration.Short
            )

            onHandleEvent(ReminderAddEditEvent.RemoveSnackbarMessage)
        }
    }
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
                    contentDescription = stringResource(R.string.cd_back)
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
            Box {
                DropdownMenu(
                    expanded = showOverflowMenu,
                    onDismissRequest = { showOverflowMenu = false }
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = stringResource(R.string.dropdown_delete_reminder),
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 17.sp)
                            )
                        },
                        onClick = {
                            showOverflowMenu = false
                            onHandleEvent(ReminderAddEditEvent.DeleteReminder(reminder))
                            onNavigateUp()
                        }
                    )
                    if (!reminder.isCompleted) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(R.string.dropdown_complete_reminder),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 17.sp
                                    )
                                )
                            },
                            onClick = {
                                showOverflowMenu = false
                                onHandleEvent(ReminderAddEditEvent.CompleteReminder(reminder))
                                onNavigateUp()
                            }
                        )
                    }
                }
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
        ReminderDetailsScreen(
            uiState = ReminderAddEditUiState(
                reminder = reminder,
                initialReminder = reminder,
                isReminderValid = true
            ),
            onHandleEvent = {},
            onNavigateUp = {}
        )
    }
}
