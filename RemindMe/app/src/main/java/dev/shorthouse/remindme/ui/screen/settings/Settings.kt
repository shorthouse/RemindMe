package dev.shorthouse.remindme.ui.screen.settings

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Launch
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.shorthouse.remindme.BuildConfig
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.data.protodatastore.ThemeStyle
import dev.shorthouse.remindme.ui.component.dialog.RemindMeAlertDialog
import dev.shorthouse.remindme.ui.component.progressindicator.CenteredCircularProgressIndicator
import dev.shorthouse.remindme.ui.component.topappbar.RemindMeTopAppBar
import dev.shorthouse.remindme.ui.theme.AppTheme

@Composable
@Destination
fun SettingsScreen(
    navigator: DestinationsNavigator,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsScreen(
        uiState = uiState,
        onHandleEvent = { viewModel.handleEvent(it) },
        onNavigateUp = { navigator.navigateUp() }
    )
}

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onHandleEvent: (SettingsEvent) -> Unit,
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
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                }
            )
        },
        content = { scaffoldPadding ->
            if (uiState.isLoading) {
                CenteredCircularProgressIndicator()
            } else {
                SettingsContent(
                    themeStyle = uiState.themeStyle,
                    onThemeStyleChange = { onHandleEvent(SettingsEvent.Theme(it)) },
                    isNotificationDefaultOn = uiState.isNotificationDefaultOn,
                    onNotificationDefaultChange = {
                        onHandleEvent(SettingsEvent.NotificationDefault(it))
                    },
                    modifier = Modifier.padding(scaffoldPadding)
                )
            }
        },
        modifier = modifier
    )
}

@Composable
fun SettingsContent(
    themeStyle: ThemeStyle,
    onThemeStyleChange: (ThemeStyle) -> Unit,
    isNotificationDefaultOn: Boolean,
    onNotificationDefaultChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SettingsGroupCustomisation(
            themeStyle,
            onThemeStyleChange,
            isNotificationDefaultOn,
            onNotificationDefaultChange
        )

        Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

        SettingsGroupAbout()
    }
}

@Composable
private fun SettingsGroupCustomisation(
    themeStyle: ThemeStyle,
    onThemeStyleChange: (ThemeStyle) -> Unit,
    isNotificationDefaultOn: Boolean,
    onNotificationDefaultChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    SettingsGroup(
        header = stringResource(R.string.settings_header_customisation),
        content = {
            var isThemeStyleDialogOpen by remember { mutableStateOf(false) }

            SettingsOption(
                title = stringResource(R.string.settings_title_theme),
                subtitle = stringResource(themeStyle.nameStringId),
                action = {
                    Icon(
                        imageVector = Icons.Rounded.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                onClick = { isThemeStyleDialogOpen = true }
            )

            if (isThemeStyleDialogOpen) {
                var selectedThemeOption by remember { mutableStateOf(themeStyle) }

                RemindMeAlertDialog(
                    title = stringResource(R.string.dialog_title_app_theme),
                    confirmText = stringResource(R.string.dialog_action_apply),
                    onConfirm = { onThemeStyleChange(selectedThemeOption) },
                    onDismiss = { isThemeStyleDialogOpen = false },
                    content = {
                        Column {
                            ThemeStyle.values().forEach { themeStyleOption ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier
                                        .selectable(
                                            selected = (themeStyleOption == selectedThemeOption),
                                            onClick = { selectedThemeOption = themeStyleOption },
                                            role = Role.RadioButton
                                        )
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                ) {
                                    RadioButton(
                                        selected = (themeStyleOption == selectedThemeOption),
                                        onClick = null
                                    )

                                    Text(
                                        text = stringResource(themeStyleOption.nameStringId),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                )
            }

            SettingsOption(
                title = stringResource(R.string.settings_title_notification_behaviour),
                subtitle = if (isNotificationDefaultOn) {
                    stringResource(R.string.settings_subtitle_notification_on)
                } else {
                    stringResource(R.string.settings_subtitle_notification_off)
                },
                action = {
                    Switch(
                        checked = isNotificationDefaultOn,
                        onCheckedChange = { onNotificationDefaultChange(it) }
                    )
                },
                onClick = { onNotificationDefaultChange(!isNotificationDefaultOn) }
            )
        },
        modifier = modifier
    )
}

@Composable
private fun SettingsGroupAbout(modifier: Modifier = Modifier) {
    SettingsGroup(
        header = stringResource(R.string.settings_header_about),
        content = {
            SettingsOption(
                title = stringResource(R.string.settings_title_version_number),
                subtitle = BuildConfig.VERSION_NAME,
                onClick = {}
            )

            val uriHandler = LocalUriHandler.current
            val uri = stringResource(R.string.app_url)

            SettingsOption(
                title = stringResource(R.string.settings_title_source_code),
                subtitle = stringResource(R.string.settings_subtitle_github),
                action = {
                    Icon(
                        imageVector = Icons.Rounded.Launch,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                onClick = { uriHandler.openUri(uri) }
            )
        },
        modifier = modifier
    )
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
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = modifier.padding(
                start = 8.dp,
                top = 16.dp,
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
    action: @Composable () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(modifier = Modifier.weight(0.8f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                fontWeight = FontWeight.Medium
            )
        }

        action()
    }
}

@Composable
@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun SettingsPreview() {
    AppTheme {
        SettingsScreen(
            uiState = SettingsUiState(isNotificationDefaultOn = true),
            onHandleEvent = {},
            onNavigateUp = {}
        )
    }
}
