package dev.shorthouse.remindme.compose.component

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.state.BottomSheetItem
import dev.shorthouse.remindme.theme.RemindMeTheme

@Composable
fun BottomSheetNavigate(
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomSheetItem(
            Icons.Rounded.NotificationImportant,
            stringResource(R.string.overdue_reminders)
        ),
        BottomSheetItem(
            Icons.Rounded.NotificationsActive,
            stringResource(R.string.scheduled_reminders)
        ),
        BottomSheetItem(
            Icons.Rounded.NotificationsNone,
            stringResource(R.string.completed_reminders)
        )
    )

    BottomSheet(
        title = stringResource(R.string.app_name),
        items = items,
        selectedIndex = selectedIndex,
        onSelected = onSelected,
        modifier = modifier
    )
}

@Composable
fun BottomSheetSort(
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomSheetItem(
            Icons.Rounded.ExpandLess,
            stringResource(R.string.drawer_title_earliest_date_first)
        ),
        BottomSheetItem(
            Icons.Rounded.ExpandMore,
            stringResource(R.string.drawer_title_latest_date_first)
        )
    )

    BottomSheet(
        title = stringResource(R.string.nav_drawer_sort_title),
        items = items,
        selectedIndex = selectedIndex,
        onSelected = onSelected,
        modifier = modifier
    )
}

@Composable
fun BottomSheet(
    title: String,
    items: List<BottomSheetItem>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
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
                style = MaterialTheme.typography.h6,
                modifier = Modifier
                    .padding(start = 8.dp, bottom = 12.dp, top = 12.dp)
            )

            items.forEachIndexed { index, bottomSheetItem ->
                BottomSheetButton(
                    buttonIcon = bottomSheetItem.icon,
                    buttonLabel = bottomSheetItem.label,
                    isSelected = selectedIndex == index,
                    onSelected = { onSelected(index) }
                )
            }
        }
    }
}

@Composable
fun BottomSheetButton(
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

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun BottomSheetNavigatePreview() {
    RemindMeTheme {
        var selectedNavigateIndex by remember { mutableStateOf(0) }

        BottomSheetNavigate(
            selectedIndex = selectedNavigateIndex,
            onSelected = { selectedNavigateIndex = it }
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
            selectedIndex = selectedSortIndex,
            onSelected = { selectedSortIndex = it }
        )
    }
}
