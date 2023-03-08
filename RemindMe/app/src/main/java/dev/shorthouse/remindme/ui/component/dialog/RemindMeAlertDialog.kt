package dev.shorthouse.remindme.ui.component.dialog

import android.content.res.Configuration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.ui.theme.m3.AppTheme

@Composable
fun RemindMeAlertDialog(
    title: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = confirmText
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.dialog_action_cancel)
                )
            }
        },
        onDismissRequest = onDismiss
    )
}

@Composable
@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
fun RemindMeAlertDialogPreview() {
    AppTheme {
        val title = "Do this action?"
        val confirmText = "Confirm"

        RemindMeAlertDialog(
            title = title,
            confirmText = confirmText,
            onConfirm = {},
            onDismiss = {}
        )
    }
}
