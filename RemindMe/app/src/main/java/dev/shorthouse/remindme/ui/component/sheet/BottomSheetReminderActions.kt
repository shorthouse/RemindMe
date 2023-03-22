package dev.shorthouse.remindme.ui.component.sheet

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.TaskAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.ui.state.ReminderState
import dev.shorthouse.remindme.ui.util.enums.ReminderAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetReminderActions(
    reminderState: ReminderState,
    onReminderActionSelected: (ReminderAction) -> Unit,
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        tonalElevation = dimensionResource(R.dimen.margin_none),
        dragHandle = null
    ) {
        SheetReminderActions(
            reminderState = reminderState,
            onReminderActionItemSelected = onReminderActionSelected
        )
    }
}
