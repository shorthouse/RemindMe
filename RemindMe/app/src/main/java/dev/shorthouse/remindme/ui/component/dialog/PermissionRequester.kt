package dev.shorthouse.remindme.ui.component.dialog

import android.Manifest
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun NotificationPermissionRequester() {
    if (Build.VERSION.SDK_INT >= 33) {
        val notificationPermissionState = rememberPermissionState(
            Manifest.permission.POST_NOTIFICATIONS
        )

        if (!notificationPermissionState.status.isGranted) {
            LaunchedEffect(Unit) {
                notificationPermissionState.launchPermissionRequest()
            }
        }
    }
}
