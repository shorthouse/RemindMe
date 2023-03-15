package dev.shorthouse.remindme.ui.component.sheet

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.rounded.ChecklistRtl
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.TaskAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.ui.previewdata.DefaultReminderProvider
import dev.shorthouse.remindme.ui.state.ReminderState
import dev.shorthouse.remindme.ui.theme.AppTheme
import dev.shorthouse.remindme.ui.util.enums.ReminderAction

@Composable
fun BottomSheetReminderActions(
    reminderState: ReminderState,
    onReminderActionItemSelected: (ReminderAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val reminderActionItems = buildList {
        if (!reminderState.isCompleted) {
            if (reminderState.isRepeatReminder) {
                add(
                    BottomSheetActionItem(
                        icon = Icons.Rounded.TaskAlt,
                        label = stringResource(R.string.drawer_item_complete),
                        action = ReminderAction.COMPLETE_REPEAT_OCCURRENCE
                    )
                )
                add(
                    BottomSheetActionItem(
                        icon = Icons.Rounded.ChecklistRtl,
                        label = stringResource(R.string.drawer_item_complete_series),
                        action = ReminderAction.COMPLETE_REPEAT_SERIES
                    )
                )
            } else {
                add(
                    BottomSheetActionItem(
                        icon = Icons.Rounded.TaskAlt,
                        label = stringResource(R.string.drawer_item_complete),
                        action = ReminderAction.COMPLETE_ONETIME
                    )
                )
            }
            add(
                BottomSheetActionItem(
                    icon = Icons.Outlined.Edit,
                    label = stringResource(R.string.drawer_item_edit),
                    action = ReminderAction.EDIT
                )
            )
        }
        add(
            BottomSheetActionItem(
                icon = Icons.Rounded.DeleteOutline,
                label = stringResource(R.string.drawer_item_delete),
                action = ReminderAction.DELETE
            )
        )
    }

    Surface(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.margin_tiny))
        ) {
            Text(
                text = reminderState.name,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Medium),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(
                        start = dimensionResource(R.dimen.margin_tiny),
                        bottom = dimensionResource(R.dimen.margin_small),
                        top = dimensionResource(R.dimen.margin_small)
                    )
            )

            reminderActionItems.forEach { bottomSheetItem ->
                BottomSheetButton(
                    buttonIcon = bottomSheetItem.icon,
                    buttonLabel = bottomSheetItem.label,
                    onSelected = { onReminderActionItemSelected(bottomSheetItem.action) }
                )
            }
        }
    }
}

@Composable
fun BottomSheetButton(
    buttonIcon: ImageVector,
    buttonLabel: String,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onSelected)
            .testTag(buttonLabel)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(
                    horizontal = dimensionResource(R.dimen.margin_tiny),
                    vertical = dimensionResource(R.dimen.margin_small)
                )
        ) {
            Icon(
                imageVector = buttonIcon,
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = null
            )

            Spacer(Modifier.width(dimensionResource(R.dimen.margin_normal)))

            Text(
                text = buttonLabel,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodySmall
            )
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
fun BottomSheetReminderActionsPreview(
    @PreviewParameter(DefaultReminderProvider::class) reminderState: ReminderState
) {
    AppTheme {
        BottomSheetReminderActions(
            reminderState = reminderState,
            onReminderActionItemSelected = {}
        )
    }
}
