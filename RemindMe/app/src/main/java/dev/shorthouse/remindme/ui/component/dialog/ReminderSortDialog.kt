package dev.shorthouse.remindme.ui.component.dialog

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.data.protodatastore.ReminderSort
import dev.shorthouse.remindme.ui.theme.AppTheme

@Composable
fun ReminderSortDialog(
    initialSort: ReminderSort,
    onApplySort: (ReminderSort) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSortOption by remember { mutableStateOf(initialSort) }
    val sortOptions = ReminderSort.values()

    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            modifier = modifier
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(12.dp)
                    .fillMaxWidth(0.9f)
            ) {
                Text(
                    text = stringResource(R.string.sort_dialog_title),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                sortOptions.forEach { sortOption ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .selectable(
                                selected = (sortOption == selectedSortOption),
                                onClick = { selectedSortOption = sortOption },
                                role = Role.RadioButton
                            )
                            .fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = (sortOption == selectedSortOption),
                            onClick = { selectedSortOption = sortOption }
                        )

                        Text(
                            text = stringResource(sortOption.nameStringId),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                ) {
                    TextButton(onClick = {
                        onApplySort(selectedSortOption)
                        onDismiss()
                    }) {
                        Text(
                            text = stringResource(R.string.dialog_action_apply),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
fun ReminderSortDialogPreview() {
    AppTheme {
        ReminderSortDialog(
            initialSort = ReminderSort.BY_EARLIEST_DATE_FIRST,
            onApplySort = {},
            onDismiss = {}
        )
    }
}
