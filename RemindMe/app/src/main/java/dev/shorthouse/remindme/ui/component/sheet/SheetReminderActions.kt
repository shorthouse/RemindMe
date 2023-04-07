package dev.shorthouse.remindme.ui.component.sheet

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.rounded.ChecklistRtl
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.TaskAlt
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.ui.previewprovider.DefaultReminderProvider
import dev.shorthouse.remindme.ui.theme.AppTheme
import dev.shorthouse.remindme.ui.util.enums.ReminderAction

@Composable
fun SheetReminderActions(
    reminder: Reminder,
    onReminderActionSelected: (ReminderAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val reminderActionItems = buildList {
        if (!reminder.isCompleted) {
            if (reminder.isRepeatReminder()) {
                add(
                    BottomSheetActionItem(
                        icon = Icons.Rounded.TaskAlt,
                        label = stringResource(R.string.sheet_action_complete),
                        action = ReminderAction.COMPLETE_REPEAT_OCCURRENCE
                    )
                )
                add(
                    BottomSheetActionItem(
                        icon = Icons.Rounded.ChecklistRtl,
                        label = stringResource(R.string.sheet_action_complete_series),
                        action = ReminderAction.COMPLETE_REPEAT_SERIES
                    )
                )
            } else {
                add(
                    BottomSheetActionItem(
                        icon = Icons.Rounded.TaskAlt,
                        label = stringResource(R.string.sheet_action_complete),
                        action = ReminderAction.COMPLETE_ONETIME
                    )
                )
            }
            add(
                BottomSheetActionItem(
                    icon = Icons.Outlined.Edit,
                    label = stringResource(R.string.sheet_action_edit),
                    action = ReminderAction.EDIT
                )
            )
        }
        add(
            BottomSheetActionItem(
                icon = Icons.Rounded.DeleteOutline,
                label = stringResource(R.string.sheet_action_delete),
                action = ReminderAction.DELETE
            )
        )
    }

    Surface(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = reminder.name,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Medium),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(
                        start = 8.dp,
                        bottom = 12.dp,
                        top = 12.dp
                    )
            )

            reminderActionItems.forEach { bottomSheetItem ->
                SheetButton(
                    buttonIcon = bottomSheetItem.icon,
                    buttonLabel = bottomSheetItem.label,
                    onSelected = { onReminderActionSelected(bottomSheetItem.action) }
                )
            }
        }
    }
}

data class BottomSheetActionItem(
    val icon: ImageVector,
    val label: String,
    val action: ReminderAction
)

@Composable
@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
fun SheetReminderActionsPreview(
    @PreviewParameter(DefaultReminderProvider::class) reminder: Reminder
) {
    AppTheme {
        SheetReminderActions(
            reminder = reminder,
            onReminderActionSelected = {}
        )
    }
}
