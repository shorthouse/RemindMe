package dev.shorthouse.remindme.ui.component.chip

import android.content.res.Configuration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.shorthouse.remindme.ui.theme.AppTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun RemindMeInputChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    InputChip(
        selected = selected,
        onClick = onClick,
        label = label,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        colors = InputChipDefaults.inputChipColors(
            leadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            labelColor = MaterialTheme.colorScheme.onSurface,
            trailingIconColor = MaterialTheme.colorScheme.onSurface,
            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
            selectedTrailingIconColor = MaterialTheme.colorScheme.onPrimary,
            selectedContainerColor = MaterialTheme.colorScheme.primary
        ),
        border = InputChipDefaults.inputChipBorder(
            borderWidth = 1.5.dp,
            disabledBorderColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = modifier
    )
}

@Composable
@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
private fun RemindMeUnselectedInputChipPreview() {
    RemindMeInputChip(
        selected = false,
        onClick = { },
        label = { Text("Unselected") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.NotificationsNone,
                contentDescription = null
            )
        },
        trailingIcon = null
    )
}

@Composable
@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
private fun RemindMeSelectedInputChipPreview() {
    AppTheme {
        RemindMeInputChip(
            selected = true,
            onClick = { },
            label = { Text("Unselected") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.NotificationsNone,
                    contentDescription = null
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null
                )
            }
        )
    }
}
