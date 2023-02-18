package dev.shorthouse.remindme.compose.screen

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.component.dialog.RemindMeAlertDialog
import dev.shorthouse.remindme.compose.component.emptystate.EmptyStateCompletedReminders
import dev.shorthouse.remindme.compose.component.list.ReminderListContent
import dev.shorthouse.remindme.compose.component.sheet.BottomSheetReminderActions
import dev.shorthouse.remindme.compose.previewdata.ReminderListProvider
import dev.shorthouse.remindme.compose.screen.destinations.ReminderEditScreenDestination
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.theme.RemindMeTheme
import dev.shorthouse.remindme.theme.Scrim
import dev.shorthouse.remindme.viewmodel.ListCompletedViewModel
import dev.shorthouse.remindme.viewmodel.ListViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalLifecycleComposeApi::class)
@Destination
@Composable
fun ReminderListCompletedScreen(
    listCompletedViewModel: ListCompletedViewModel = hiltViewModel(),
    listViewModel: ListViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val uiState by listCompletedViewModel.uiState.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()

    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    var selectedReminderState by remember { mutableStateOf(ReminderState()) }

    ModalBottomSheetLayout(
        content = {
            ReminderListCompletedScaffold(
                completedReminderStates = uiState.completedReminderStates,
                onNavigateUp = { navigator.navigateUp() },
                onDeleteCompletedReminders = { listCompletedViewModel.deleteCompletedReminders() },
                onReminderCard = { reminderState ->
                    selectedReminderState = reminderState
                    coroutineScope.launch { bottomSheetState.show() }
                },
                isLoading = uiState.isLoading
            )
        },
        sheetContent = {
            BackHandler(enabled = bottomSheetState.isVisible) {
                coroutineScope.launch { bottomSheetState.hide() }
            }

            BottomSheetReminderActions(
                reminderState = selectedReminderState,
                onReminderActionItemSelected = { reminderAction ->
                    coroutineScope.launch {
                        bottomSheetState.hide()

                        listViewModel.processReminderAction(
                            selectedReminderState = selectedReminderState.copy(),
                            reminderAction = reminderAction,
                            onEdit = {
                                navigator.navigate(ReminderEditScreenDestination(reminderId = selectedReminderState.id))
                            }
                        )
                    }
                }
            )
        },
        sheetState = bottomSheetState,
        scrimColor = Scrim,
    )
}

@Composable
fun ReminderListCompletedScaffold(
    completedReminderStates: List<ReminderState>,
    onNavigateUp: () -> Unit,
    onDeleteCompletedReminders: () -> Unit,
    onReminderCard: (ReminderState) -> Unit,
    isLoading: Boolean
) {
    Scaffold(
        topBar = {
            ReminderListCompletedTopBar(
                onNavigateUp = onNavigateUp,
                onDeleteCompletedReminders = onDeleteCompletedReminders
            )
        },
        content = { scaffoldPadding ->
            if (!isLoading) {
                val modifier = Modifier.padding(scaffoldPadding)

                ReminderListContent(
                    reminderStates = completedReminderStates,
                    emptyStateContent = { EmptyStateCompletedReminders() },
                    onReminderCard = onReminderCard,
                    contentPadding = PaddingValues(dimensionResource(R.dimen.margin_tiny)),
                    modifier = modifier
                )
            }
        }
    )
}

@Composable
fun ReminderListCompletedTopBar(
    onNavigateUp: () -> Unit,
    onDeleteCompletedReminders: () -> Unit
) {
    var isDeleteAllDialogOpen by remember { mutableStateOf(false) }
    if (isDeleteAllDialogOpen) {
        RemindMeAlertDialog(
            title = stringResource(R.string.alert_dialog_title_delete_all_completed),
            confirmText = stringResource(R.string.dialog_action_delete),
            onConfirm = {
                isDeleteAllDialogOpen = false
                onDeleteCompletedReminders()
            },
            onDismiss = { isDeleteAllDialogOpen = false }
        )
    }

    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.top_bar_title_completed_reminders),
                style = MaterialTheme.typography.h6,
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.cd_top_app_bar_back),
                )
            }
        },
        actions = {
            IconButton(onClick = { isDeleteAllDialogOpen = true }) {
                Icon(
                    imageVector = Icons.Rounded.DeleteOutline,
                    contentDescription = stringResource(R.string.cd_top_bar_delete_completed_reminders),
                    tint = MaterialTheme.colors.onPrimary
                )
            }
        }
    )
}

@Composable
@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun ReminderListCompletedPreview(
    @PreviewParameter(ReminderListProvider::class) reminderStates: List<ReminderState>
) {
    RemindMeTheme {
        ReminderListCompletedScaffold(
            completedReminderStates = reminderStates,
            onNavigateUp = {},
            onDeleteCompletedReminders = {},
            onReminderCard = {},
            isLoading = false,
        )
    }
}
