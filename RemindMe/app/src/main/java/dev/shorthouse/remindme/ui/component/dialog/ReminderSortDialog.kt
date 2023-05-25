package dev.shorthouse.remindme.ui.component.dialog

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
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

    RemindMeAlertDialog(
        title = stringResource(R.string.dialog_title_sort),
        content = {
            ReminderSortDialogContent(
                sortOptions = sortOptions,
                selectedSortOption = selectedSortOption,
                onSelectedSortOptionChange = { selectedSortOption = it }
            )
        },
        confirmText = stringResource(R.string.dialog_action_apply),
        onConfirm = {
            onApplySort(selectedSortOption)
            onDismiss()
        },
        onDismiss = onDismiss,
        modifier = modifier
    )
}

@Composable
private fun ReminderSortDialogContent(
    sortOptions: Array<ReminderSort>,
    selectedSortOption: ReminderSort,
    onSelectedSortOptionChange: (ReminderSort) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.selectableGroup()) {
        sortOptions.forEach { sortOption ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .selectable(
                        selected = (sortOption == selectedSortOption),
                        onClick = { onSelectedSortOptionChange(sortOption) },
                        role = Role.RadioButton
                    )
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                RadioButton(
                    selected = (sortOption == selectedSortOption),
                    onClick = null,
                    modifier = Modifier.padding(8.dp)
                )

                Text(
                    text = stringResource(sortOption.nameStringId),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun ReminderSortDialogPreview() {
    AppTheme {
        ReminderSortDialog(
            initialSort = ReminderSort.BY_EARLIEST_DATE_FIRST,
            onApplySort = {},
            onDismiss = {}
        )
    }
}
