package dev.shorthouse.remindme.compose.component.dialog

import android.content.res.Configuration
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.theme.RemindMeTheme

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
                style = MaterialTheme.typography.body1
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = confirmText,
                    style = MaterialTheme.typography.button,
                    color = MaterialTheme.colors.primary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.dialog_action_cancel),
                    style = MaterialTheme.typography.button,
                    color = MaterialTheme.colors.primary
                )
            }
        },
        onDismissRequest = onDismiss,
    )
}

@Composable
@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
fun RemindMeAlertDialogPreview() {
    RemindMeTheme {
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
