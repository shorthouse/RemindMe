package dev.shorthouse.remindme.ui.settings

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Launch
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.shorthouse.remindme.BuildConfig
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.ui.component.topappbar.RemindMeTopAppBar
import dev.shorthouse.remindme.ui.theme.AppTheme

@Destination
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsScreen(
        uiState = uiState,
        onNavigateUp = { navigator.navigateUp() }
    )
}

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            RemindMeTopAppBar(
                title = stringResource(R.string.top_app_bar_title_settings),
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.cd_top_app_bar_back)
                        )
                    }
                }
            )
        },
        content = { scaffoldPadding ->
            if (!uiState.isLoading) {
                SettingsContent(
                    modifier = Modifier.padding(scaffoldPadding)
                )
            }
        },
        modifier = modifier
    )
}

@Composable
fun SettingsContent(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        SettingsGroup(
            header = stringResource(R.string.settings_header_personalisation),
            content = {
                SettingsOption(
                    title = stringResource(R.string.settings_title_theme),
                    subtitle = "Follow system",
                    actionIcon = Icons.Rounded.ChevronRight,
                    onClick = {}
                )

                SettingsOption(
                    title = stringResource(R.string.settings_title_date_format),
                    subtitle = "Fri, 28th July 2023",
                    actionIcon = Icons.Rounded.ChevronRight,
                    onClick = {}
                )

                SettingsOption(
                    title = stringResource(R.string.settings_title_notification_behaviour),
                    subtitle = "Off by default when adding reminder",
                    actionIcon = Icons.Rounded.ChevronRight,
                    onClick = {}
                )
            }
        )

        Divider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        )

        SettingsGroup(
            header = stringResource(R.string.settings_header_about),
            content = {
                SettingsOption(
                    title = stringResource(R.string.settings_title_version_number),
                    subtitle = BuildConfig.VERSION_NAME,
                    onClick = {}
                )

                val uriHandler = LocalUriHandler.current
                SettingsOption(
                    title = stringResource(R.string.settings_title_source_code),
                    subtitle = stringResource(R.string.settings_subtitle_github),
                    actionIcon = Icons.Rounded.Launch,
                    onClick = { uriHandler.openUri("https://github.com/shorthouse/RemindMe") }
                )
            }
        )
    }
}

@Composable
fun SettingsGroup(
    header: String,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Column {
        Text(
            text = header,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = modifier.padding(
                start = 8.dp,
                top = 8.dp,
                end = 8.dp,
                bottom = 0.dp
            )
        )

        content()
    }
}

@Composable
fun SettingsOption(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    actionIcon: ImageVector? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                ),
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(Modifier.weight(1f))

        actionIcon?.let {
            Icon(
                imageVector = actionIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
            )
        }
    }
}

@Composable
@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
fun ReminderListPreview() {
    AppTheme {
        SettingsScreen(
            uiState = SettingsUiState(),
            onNavigateUp = {}
        )
    }
}
