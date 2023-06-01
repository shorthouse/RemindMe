package dev.shorthouse.remindme.ui.component.snackbar

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RemindMeSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier
) {
    Snackbar(
        shape = MaterialTheme.shapes.small,
        modifier = modifier.padding(12.dp)
    ) {
        Text(
            text = snackbarData.visuals.message,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
