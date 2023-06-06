package dev.shorthouse.remindme.ui.screen.addedit.add

import android.content.res.Configuration
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.ui.previewprovider.EmptyReminderProvider
import dev.shorthouse.remindme.ui.screen.addedit.ReminderAddEditContent
import dev.shorthouse.remindme.ui.screen.addedit.ReminderAddEditEvent
import dev.shorthouse.remindme.ui.screen.addedit.ReminderAddEditUiState
import dev.shorthouse.remindme.ui.screen.addedit.ReminderAddEditViewModel
import dev.shorthouse.remindme.ui.theme.AppTheme
import dev.shorthouse.remindme.util.disableBottomSheetSwipe

@Composable
fun ReminderAddBottomSheet(
    viewModel: ReminderAddEditViewModel = hiltViewModel(),
    onDismissSheet: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ReminderAddBottomSheet(
        uiState = uiState,
        onHandleEvent = { viewModel.handleEvent(it) },
        onDismissRequest = onDismissSheet
    )

    DisposableEffect(Unit) {
        viewModel.setAddReminder()
        onDispose { viewModel.handleEvent(ReminderAddEditEvent.ResetState) }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ReminderAddBottomSheet(
    uiState: ReminderAddEditUiState,
    onHandleEvent: (ReminderAddEditEvent) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        ),
        shape = MaterialTheme.shapes.large.copy(
            bottomStart = CornerSize(0.dp),
            bottomEnd = CornerSize(0.dp)
        ),
        tonalElevation = 0.dp,
        dragHandle = null,
        modifier = modifier
    ) {
        if (!uiState.isLoading) {
            ReminderAddEditContent(
                reminder = uiState.reminder,
                onHandleEvent = onHandleEvent,
                onNavigateUp = onDismissRequest,
                isReminderValid = uiState.isReminderValid,
                modifier = Modifier.disableBottomSheetSwipe()
            )
        }
    }
}

@Composable
@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun AddReminderBottomSheetPreview(
    @PreviewParameter(EmptyReminderProvider::class) reminder: Reminder
) {
    AppTheme {
        ReminderAddBottomSheet(
            uiState = ReminderAddEditUiState(initialReminder = reminder),
            onHandleEvent = {},
            onDismissRequest = {}
        )
    }
}
