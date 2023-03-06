package dev.shorthouse.remindme.ui.screen.list.completed

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import dev.shorthouse.remindme.ui.component.dialog.RemindMeAlertDialog
import dev.shorthouse.remindme.ui.component.emptystate.EmptyStateCompletedReminders
import dev.shorthouse.remindme.ui.component.list.ReminderListContent
import dev.shorthouse.remindme.ui.component.sheet.BottomSheetReminderActions
import dev.shorthouse.remindme.ui.preview.ReminderListProvider
import dev.shorthouse.remindme.ui.screen.destinations.ReminderEditScreenDestination
import dev.shorthouse.remindme.ui.screen.list.ListViewModel
import dev.shorthouse.remindme.ui.state.ReminderState
import dev.shorthouse.remindme.ui.theme.m2.RemindMeTheme
import dev.shorthouse.remindme.ui.theme.m2.Scrim
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalLifecycleComposeApi::class)
@Destination
@Composable
fun ReminderListCompletedScreen(
    listCompletedViewModel: ListCompletedViewModel = hiltViewModel(),
    listViewModel: ListViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    listCompletedViewModel.initialiseUiState()
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
                                navigator.navigate(
                                    ReminderEditScreenDestination(
                                        reminderId = selectedReminderState.id
                                    )
                                )
                            }
                        )
                    }
                }
            )
        },
        sheetState = bottomSheetState,
        scrimColor = Scrim
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
                style = MaterialTheme.typography.h6
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.cd_top_app_bar_back)
                )
            }
        },
        actions = {
            IconButton(onClick = { isDeleteAllDialogOpen = true }) {
                Icon(
                    imageVector = Icons.Rounded.DeleteOutline,
                    contentDescription = stringResource(
                        R.string.cd_top_bar_delete_completed_reminders
                    ),
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
            isLoading = false
        )
    }
}
