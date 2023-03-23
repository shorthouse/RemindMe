package dev.shorthouse.remindme.ui.component.sheet

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.ui.theme.AppTheme

@Composable
fun BottomSheetButton(
    buttonIcon: ImageVector,
    buttonLabel: String,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onSelected)
            .testTag(buttonLabel)
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
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = null
            )

            Spacer(Modifier.width(dimensionResource(R.dimen.margin_normal)))

            Text(
                text = buttonLabel,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
@Preview(name = "Light Mode", showBackground = true, widthDp = 300)
@Preview(
    name = "Dark Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    widthDp = 300
)
fun BottomSheetButtonPreview() {
    AppTheme {
        BottomSheetButton(
            buttonIcon = Icons.Rounded.Edit,
            buttonLabel = "Edit",
            onSelected = {}
        )
    }
}
