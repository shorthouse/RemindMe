package dev.shorthouse.remindme.compose.component

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.preview.PreviewData
import dev.shorthouse.remindme.compose.state.BottomSheetActionItem
import dev.shorthouse.remindme.compose.state.BottomSheetSelectableItem
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.theme.RemindMeTheme
import dev.shorthouse.remindme.utilities.enums.ReminderAction

@Composable
fun BottomSheetNavigate(
    selectedItemIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomSheetSelectableItem(
            Icons.Rounded.NotificationImportant,
            stringResource(R.string.overdue_reminders)
        ),
        BottomSheetSelectableItem(
            Icons.Rounded.NotificationsActive,
            stringResource(R.string.scheduled_reminders)
        ),
        BottomSheetSelectableItem(
            Icons.Rounded.NotificationsNone,
            stringResource(R.string.completed_reminders)
        )
    )

    BottomSheetSelectable(
        title = stringResource(R.string.app_name),
        items = items,
        selectedItemIndex = selectedItemIndex,
        onItemSelected = onItemSelected,
        modifier = modifier
    )
}

@Composable
fun BottomSheetSort(
    selectedItemIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomSheetSelectableItem(
            Icons.Rounded.ExpandLess,
            stringResource(R.string.drawer_title_earliest_date_first)
        ),
        BottomSheetSelectableItem(
            Icons.Rounded.ExpandMore,
            stringResource(R.string.drawer_title_latest_date_first)
        )
    )

    BottomSheetSelectable(
        title = stringResource(R.string.nav_drawer_sort_title),
        items = items,
        selectedItemIndex = selectedItemIndex,
        onItemSelected = onItemSelected,
        modifier = modifier
    )
}

@Composable
fun BottomSheetSelectable(
    title: String,
    items: List<BottomSheetSelectableItem>,
    selectedItemIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.h5,
                modifier = Modifier
                    .padding(start = 8.dp, bottom = 12.dp, top = 12.dp)
            )

            items.forEachIndexed { index, bottomSheetItem ->
                BottomSheetSelectableButton(
                    buttonIcon = bottomSheetItem.icon,
                    buttonLabel = bottomSheetItem.label,
                    isSelected = selectedItemIndex == index,
                    onSelected = { onItemSelected(index) }
                )
            }
        }
    }
}

@Composable
fun BottomSheetSelectableButton(
    buttonIcon: ImageVector,
    buttonLabel: String,
    isSelected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    val textIconColor = if (isSelected) {
        MaterialTheme.colors.primaryVariant
    } else {
        MaterialTheme.colors.onSurface
    }

    val backgroundColor = if (isSelected) {
        MaterialTheme.colors.primary.copy(alpha = 0.12f)
    } else {
        Color.Transparent
    }

    Surface(
        color = backgroundColor,
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
                tint = textIconColor,
                contentDescription = null
            )

            Spacer(Modifier.width(dimensionResource(R.dimen.margin_normal)))

            Text(
                text = buttonLabel,
                color = textIconColor,
                style = MaterialTheme.typography.caption
            )
        }
    }
}

@Composable
fun BottomSheetReminderActions(
    reminderState: ReminderState,
    onItemSelected: (ReminderAction) -> Unit,
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
                style = MaterialTheme.typography.h5,
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
                BottomSheetActionButton(
                    buttonIcon = bottomSheetItem.icon,
                    buttonLabel = bottomSheetItem.label,
                    onSelected = { onItemSelected(bottomSheetItem.action) }
                )
            }
        }
    }
}

@Composable
fun BottomSheetActionButton(
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
                tint = MaterialTheme.colors.onSurface,
                contentDescription = null
            )

            Spacer(Modifier.width(dimensionResource(R.dimen.margin_normal)))

            Text(
                text = buttonLabel,
                color = MaterialTheme.colors.onSurface,
                style = MaterialTheme.typography.caption
            )
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun BottomSheetNavigatePreview() {
    RemindMeTheme {
        var selectedNavigateIndex by remember { mutableStateOf(0) }

        BottomSheetNavigate(
            selectedItemIndex = selectedNavigateIndex,
            onItemSelected = { selectedNavigateIndex = it }
        )
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun BottomSheetSortPreview() {
    RemindMeTheme {
        var selectedSortIndex by remember { mutableStateOf(0) }

        BottomSheetSort(
            selectedItemIndex = selectedSortIndex,
            onItemSelected = { selectedSortIndex = it }
        )
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun BottomSheetReminderActionsPreview() {
    RemindMeTheme {

        BottomSheetReminderActions(
            reminderState = PreviewData.previewReminderState,
            onItemSelected = {}
        )
    }
}
