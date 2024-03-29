package dev.shorthouse.remindme.ui.component.emptystate

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.ui.theme.AppTheme

@Composable
fun EmptyStateUpcomingReminders(modifier: Modifier = Modifier) {
    EmptyState(
        painter = painterResource(R.drawable.empty_state_upcoming),
        title = stringResource(R.string.empty_state_upcoming_title),
        subtitle = stringResource(R.string.empty_state_upcoming_subtitle),
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    )
}

@Composable
fun EmptyStateOverdueReminders(modifier: Modifier = Modifier) {
    EmptyState(
        painter = painterResource(R.drawable.empty_state_overdue),
        title = stringResource(R.string.empty_state_overdue_title),
        subtitle = stringResource(R.string.empty_state_overdue_subtitle),
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
            .background(MaterialTheme.colorScheme.background)
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
            .background(MaterialTheme.colorScheme.background)
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

        Spacer(Modifier.height(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
private fun EmptyStateUpcomingRemindersPreview() {
    AppTheme {
        EmptyStateUpcomingReminders()
    }
}

@Composable
@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
private fun EmptyStateOverdueRemindersPreview() {
    AppTheme {
        EmptyStateOverdueReminders()
    }
}

@Composable
@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
private fun EmptyStateCompletedRemindersPreview() {
    AppTheme {
        EmptyStateCompletedReminders()
    }
}

@Composable
@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
private fun EmptyStateSearchRemindersPreview() {
    AppTheme {
        EmptyStateSearchReminders()
    }
}
