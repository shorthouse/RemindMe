package dev.shorthouse.remindme.ui.component.sheet

import android.content.res.Configuration
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.ui.previewdata.DefaultReminderProvider
import dev.shorthouse.remindme.ui.state.ReminderState
import dev.shorthouse.remindme.ui.theme.AppTheme
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
            onReminderActionSelected = onReminderActionSelected
        )
    }
}

@Composable
@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
fun BottomSheetReminderActionsPreview(
    @PreviewParameter(DefaultReminderProvider::class) reminderState: ReminderState
) {
    AppTheme {
        BottomSheetReminderActions(
            reminderState = ReminderState(),
            onReminderActionSelected = {},
            onDismissRequest = {}
        )
    }
}
