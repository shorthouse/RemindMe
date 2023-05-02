package dev.shorthouse.remindme.ui.component.dialog

import android.content.res.Configuration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.ui.theme.AppTheme

@Composable
fun RemindMeAlertDialog(
    title: String,
    confirmText: String,
    isConfirmEnabled: Boolean = true,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = content,
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                    onDismiss()
                },
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                    disabledContentColor = MaterialTheme.colorScheme.outline
                ),
                enabled = isConfirmEnabled
            ) {
                Text(
                    text = confirmText,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = stringResource(R.string.dialog_action_cancel),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        },
        tonalElevation = 0.dp,
        modifier = modifier
    )
}

@Composable
@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
fun RemindMeAlertDialogPreview() {
    AppTheme {
        RemindMeAlertDialog(
            title = "Dialog title",
            content = { Text("Dialog Content") },
            onConfirm = { },
            confirmText = "Confirm",
            onDismiss = { }
        )
    }
}
