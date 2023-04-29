package dev.shorthouse.remindme.ui.add

import android.content.res.Configuration
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import dev.shorthouse.remindme.ui.details.ReminderDetailsContent
import dev.shorthouse.remindme.ui.details.ReminderDetailsViewModel
import dev.shorthouse.remindme.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun AddReminderBottomSheetScreen(
    viewModel: ReminderDetailsViewModel = hiltViewModel(),
    onDismissRequest: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        ),
        shape = MaterialTheme.shapes.large.copy(
            bottomStart = CornerSize(0.dp),
            bottomEnd = CornerSize(0.dp)
        ),
        tonalElevation = 0.dp,
        dragHandle = null
    ) {
        ReminderDetailsContent(
            reminder = uiState.reminder,
            isReminderValid = viewModel.isReminderValid(uiState.reminder),
            onHandleEvent = { viewModel.handleEvent(it) },
            onNavigateUp = onDismissRequest,
            modifier = Modifier.imePadding()
        )
    }
}

@Composable
@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
fun AddReminderBottomSheetPreview() {
    AppTheme {
        AddReminderBottomSheetScreen(
            onDismissRequest = {}
        )
    }
}
