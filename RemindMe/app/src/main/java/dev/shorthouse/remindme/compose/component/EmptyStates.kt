package dev.shorthouse.remindme.compose.component

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import dev.shorthouse.remindme.R

@Composable
fun ReminderEmptyState(
    painter: Painter,
    title: String,
    subtitle: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painter,
            contentDescription = null
        )

        Spacer(Modifier.height(dimensionResource(R.dimen.margin_normal)))

        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.text_on_surface)
        )

        Text(
            text = subtitle,
            fontSize = 14.sp,
            color = colorResource(R.color.subtitle_grey)
        )
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ReminderEmptyStatePreview() {
    val painter = painterResource(R.drawable.ic_empty_state_all)
    val title = stringResource(R.string.empty_state_all_title)
    val subtitle = stringResource(R.string.empty_state_all_subtitle)

    ReminderEmptyState(
        painter = painter,
        title = title,
        subtitle = subtitle
    )
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ReminderEmptySearchStatePreview() {
    val painter = painterResource(R.drawable.ic_empty_state_search)
    val title = stringResource(R.string.empty_state_search_title)
    val subtitle = stringResource(R.string.empty_state_search_subtitle)

    ReminderEmptyState(
        painter = painter,
        title = title,
        subtitle = subtitle
    )
}
