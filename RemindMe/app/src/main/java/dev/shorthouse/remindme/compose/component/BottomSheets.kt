package dev.shorthouse.remindme.compose.component

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.material.composethemeadapter.MdcTheme
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.state.BottomSheetItem

@Composable
fun BottomSheetNavigate(
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomSheetItem(
            painterResource(R.drawable.ic_notification_active),
            stringResource(R.string.active_reminders)
        ),
        BottomSheetItem(
            painterResource(R.drawable.ic_notification),
            stringResource(R.string.all_reminders)
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
            painterResource(R.drawable.ic_sort_ascending),
            stringResource(R.string.drawer_title_earliest_date_first)
        ),
        BottomSheetItem(
            painterResource(R.drawable.ic_sort_descending),
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
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = colorResource(R.color.text_on_surface),
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
    buttonIcon: Painter,
    buttonLabel: String,
    isSelected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    val itemColor = if (isSelected) {
        colorResource(R.color.blue)
    } else {
        colorResource(R.color.text_on_surface)
    }

    val backgroundColor = if (isSelected) {
        colorResource(R.color.blue).copy(alpha = 0.12f)
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
                painter = buttonIcon,
                tint = itemColor,
                contentDescription = null
            )

            Spacer(Modifier.width(dimensionResource(R.dimen.margin_normal)))

            Text(
                text = buttonLabel,
                color = itemColor,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.25.sp,
                fontSize = 14.sp
            )
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun BottomSheetNavigatePreview() {
    var selectedBottomSheetIndex by remember { mutableStateOf(0) }

    MdcTheme {
        BottomSheetNavigate(
            selectedIndex = selectedBottomSheetIndex,
            onSelected = { selectedBottomSheetIndex = it }
        )
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun BottomSheetSortPreview() {
    var selectedBottomSheetIndex by remember { mutableStateOf(0) }

    MdcTheme {
        BottomSheetSort(
            selectedIndex = selectedBottomSheetIndex,
            onSelected = { selectedBottomSheetIndex = it }
        )
    }
}
