package dev.shorthouse.remindme.compose.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import dev.shorthouse.remindme.R

@Composable
fun NavigationBottomSheet() {
    BottomSheet(
        title = stringResource(R.string.app_name),
        bottomSheetItems = {
            BottomSheetItem(
                iconPainter = painterResource(R.drawable.ic_notification_active),
                text = "Active Reminders"
            )
            BottomSheetItem(
                iconPainter = painterResource(R.drawable.ic_notification),
                text = "All Reminders"
            )
        }
    )
}

@Composable
fun SortBottomSheet() {
    BottomSheet(
        title = stringResource(R.string.nav_drawer_sort_title),
        bottomSheetItems = {
            BottomSheetItem(
                iconPainter = painterResource(R.drawable.ic_sort_ascending),
                text = stringResource(R.string.drawer_title_earliest_date_first)
            )
            BottomSheetItem(
                iconPainter = painterResource(R.drawable.ic_sort_descending),
                text = stringResource(R.string.drawer_title_latest_date_first)
            )
        }
    )
}

@Composable
fun BottomSheet(
    title: String,
    bottomSheetItems: @Composable ColumnScope.() -> Unit = {},
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.margin_small)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.margin_normal))
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        bottomSheetItems()
    }
}

@Composable
fun BottomSheetItem(iconPainter: Painter, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(intrinsicSize = IntrinsicSize.Min)
    ) {
        Icon(
            painter = iconPainter,
            contentDescription = null,
        )

        Spacer(Modifier.width(dimensionResource(R.dimen.margin_small)))

        Text(
            text = text,
            fontSize = 18.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NavigationBottomSheetPreview() {
    NavigationBottomSheet()
}

@Preview(showBackground = true)
@Composable
fun SortBottomSheetPreview() {
    SortBottomSheet()
}
