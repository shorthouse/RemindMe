package dev.shorthouse.remindme.ui.addedit.add

import android.content.res.Configuration
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.shorthouse.remindme.ui.addedit.ReminderAddEditContent
import dev.shorthouse.remindme.ui.addedit.ReminderAddEditEvent
import dev.shorthouse.remindme.ui.addedit.ReminderAddEditViewModel
import dev.shorthouse.remindme.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderAddBottomSheet(
    viewModel: ReminderAddEditViewModel = hiltViewModel(),
    onDismissSheet: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val onDismissRequest: () -> Unit = {
        onDismissSheet()
        viewModel.handleEvent(ReminderAddEditEvent.ClearReminder)
    }

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
        dragHandle = null
    ) {
        ReminderAddEditContent(
            reminder = uiState.reminder,
            isReminderValid = viewModel.isReminderValid(uiState.reminder),
            onHandleEvent = { viewModel.handleEvent(it) },
            onNavigateUp = onDismissRequest,
            modifier = Modifier
                .imePadding()
                .disableBottomSheetSwipe()
        )
    }
}

@Composable
@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
fun AddReminderBottomSheetPreview() {
    AppTheme {
        ReminderAddBottomSheet(
            onDismissSheet = {}
        )
    }
}
