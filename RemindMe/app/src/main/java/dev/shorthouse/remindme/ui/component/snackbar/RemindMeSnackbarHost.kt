package dev.shorthouse.remindme.ui.component.snackbar

import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable

@Composable
fun RemindMeSnackbarHost(snackbarHostState: SnackbarHostState) {
    SnackbarHost(snackbarHostState) { snackbarData ->
        RemindMeSnackbar(snackbarData = snackbarData)
    }
}
