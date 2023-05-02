package dev.shorthouse.remindme.ui.component.topappbar

import android.content.res.Configuration
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.shorthouse.remindme.ui.theme.AppTheme
import dev.shorthouse.remindme.ui.theme.isLight

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun RemindMeTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    val containerColor = if (MaterialTheme.colorScheme.isLight()) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surface
    }

    val onContainerColor = if (MaterialTheme.colorScheme.isLight()) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = navigationIcon,
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
            titleContentColor = onContainerColor,
            navigationIconContentColor = onContainerColor,
            actionIconContentColor = onContainerColor
        ),
        modifier = modifier
    )
}

@Composable
@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
fun RemindMeTopAppBarPreview() {
    AppTheme {
        RemindMeTopAppBar(
            title = "Top bar title",
            navigationIcon = {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = null
                )
            },
            actions = {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = null
                )
            }
        )
    }
}
