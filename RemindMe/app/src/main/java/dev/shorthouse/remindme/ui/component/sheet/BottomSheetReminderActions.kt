package dev.shorthouse.remindme.ui.component.sheet
//
// import android.content.res.Configuration
// import androidx.compose.foundation.shape.CornerSize
// import androidx.compose.material3.ExperimentalMaterial3Api
// import androidx.compose.material3.MaterialTheme
// import androidx.compose.material3.ModalBottomSheet
// import androidx.compose.runtime.Composable
// import androidx.compose.ui.Modifier
// import androidx.compose.ui.tooling.preview.Preview
// import androidx.compose.ui.tooling.preview.PreviewParameter
// import androidx.compose.ui.unit.dp
// import dev.shorthouse.remindme.model.Reminder
// import dev.shorthouse.remindme.ui.previewprovider.DefaultReminderProvider
// import dev.shorthouse.remindme.ui.theme.AppTheme
// import dev.shorthouse.remindme.ui.util.enums.ReminderAction
//
// @OptIn(ExperimentalMaterial3Api::class)
// @Composable
// fun BottomSheetReminderActions(
//    reminder: Reminder,
//    onReminderActionSelected: (ReminderAction) -> Unit,
//    onDismissRequest: () -> Unit,
//    modifier: Modifier = Modifier
// ) {
//    ModalBottomSheet(
//        onDismissRequest = onDismissRequest,
//        shape = MaterialTheme.shapes.large.copy(
//            bottomStart = CornerSize(0.dp),
//            bottomEnd = CornerSize(0.dp)
//        ),
//        tonalElevation = 0.dp,
//        dragHandle = null,
//        modifier = modifier
//    ) {
//        SheetReminderActions(
//            reminder = reminder,
//            onReminderActionSelected = onReminderActionSelected
//        )
//    }
// }
//
// @Composable
// @Preview(name = "Light Mode", showBackground = true)
// @Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
// fun BottomSheetReminderActionsPreview(
//    @PreviewParameter(DefaultReminderProvider::class) reminder: Reminder
// ) {
//    AppTheme {
//        BottomSheetReminderActions(
//            reminder = reminder,
//            onReminderActionSelected = {},
//            onDismissRequest = {}
//        )
//    }
// }
