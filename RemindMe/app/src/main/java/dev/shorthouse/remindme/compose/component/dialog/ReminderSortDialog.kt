package dev.shorthouse.remindme.compose.component.dialog

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.theme.RemindMeTheme
import dev.shorthouse.remindme.util.enums.ReminderSortOrder

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ReminderSortDialog(
    initialSort: ReminderSortOrder,
    onApplySort: (ReminderSortOrder) -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedSortOption by remember { mutableStateOf(initialSort) }
    val sortOptions = ReminderSortOrder.values()

    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismiss
    ) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colors.surface)
                    .padding(dimensionResource(R.dimen.margin_small))
                    .fillMaxWidth(0.9f)
            ) {
                Text(
                    text = stringResource(R.string.sort_dialog_title),
                    style = MaterialTheme.typography.h5,
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    ),
                    color = MaterialTheme.colors.onSurface
                )

                sortOptions.forEach { sortOption ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RadioButton(
                            selected = (sortOption == selectedSortOption),
                            onClick = { selectedSortOption = sortOption }
                        )

                        Text(
                            text = stringResource(sortOption.displayNameStringId),
                            style = MaterialTheme.typography.body1,
                            color = MaterialTheme.colors.onSurface
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = {
                        onApplySort(selectedSortOption)
                        onDismiss()
                    }) {
                        Text(
                            text = stringResource(R.string.dialog_action_apply),
                            style = MaterialTheme.typography.button,
                            color = MaterialTheme.colors.primary,
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ReminderSortDialogPreview() {
    RemindMeTheme {
        ReminderSortDialog(
            initialSort = ReminderSortOrder.EARLIEST_DATE_FIRST,
            onApplySort = {},
            onDismiss = {}
        )
    }
}
