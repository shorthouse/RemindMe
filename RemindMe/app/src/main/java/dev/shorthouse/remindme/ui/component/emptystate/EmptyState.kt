package dev.shorthouse.remindme.ui.component.emptystate

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
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
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.ui.theme.RemindMeTheme
import dev.shorthouse.remindme.ui.theme.SubtitleGrey

@Composable
fun EmptyStateActiveReminders(modifier: Modifier = Modifier) {
    EmptyState(
        painter = painterResource(R.drawable.empty_state_active),
        title = stringResource(R.string.empty_state_active_title),
        subtitle = stringResource(R.string.empty_state_active_subtitle),
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    )
}

@Composable
fun EmptyStateCompletedReminders(modifier: Modifier = Modifier) {
    EmptyState(
        painter = painterResource(R.drawable.empty_state_completed),
        title = stringResource(R.string.empty_state_completed_title),
        subtitle = stringResource(R.string.empty_state_completed_subtitle),
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    )
}

@Composable
fun EmptyStateSearchReminders(modifier: Modifier = Modifier) {
    EmptyState(
        painter = painterResource(R.drawable.empty_state_search),
        title = stringResource(R.string.empty_state_search_title),
        subtitle = stringResource(R.string.empty_state_search_subtitle),
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    )
}

@Composable
fun EmptyState(
    painter: Painter,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Image(
            painter = painter,
            contentDescription = null
        )

        Spacer(Modifier.height(dimensionResource(R.dimen.margin_normal)))

        Text(
            text = title,
            style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Medium)
        )

        Text(
            text = subtitle,
            style = MaterialTheme.typography.caption,
            color = SubtitleGrey
        )
    }
}

@Composable
@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
fun EmptyStateActiveRemindersPreview() {
    RemindMeTheme {
        EmptyStateActiveReminders()
    }
}

@Composable
@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
fun EmptyStateCompletedRemindersPreview() {
    RemindMeTheme {
        EmptyStateCompletedReminders()
    }
}

@Composable
@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
fun EmptyStateSearchRemindersPreview() {
    RemindMeTheme {
        EmptyStateSearchReminders()
    }
}
