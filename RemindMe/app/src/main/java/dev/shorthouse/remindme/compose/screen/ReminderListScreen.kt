package dev.shorthouse.remindme.compose.screen

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.component.EmptyStateCompletedReminders
import dev.shorthouse.remindme.compose.component.EmptyStateOverdueReminders
import dev.shorthouse.remindme.compose.component.EmptyStateScheduledReminders
import dev.shorthouse.remindme.compose.preview.PreviewData
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.theme.RemindMeTheme
import dev.shorthouse.remindme.theme.SubtitleGrey
import dev.shorthouse.remindme.utilities.enums.ReminderList
import dev.shorthouse.remindme.utilities.enums.ReminderSortOrder
import dev.shorthouse.remindme.viewmodel.ReminderListViewModel

@Composable
fun ReminderListScreen(
    selectedReminderList: ReminderList,
    selectedReminderSortOrder: ReminderSortOrder,
    onReminderActions: (ReminderState) -> Unit,
    modifier: Modifier = Modifier
) {
    val reminderListViewModel: ReminderListViewModel = hiltViewModel()

    val reminderStates by reminderListViewModel
        .getReminderStates(
            selectedReminderList,
            selectedReminderSortOrder
        )
        .observeAsState()

    reminderStates?.let {
        ReminderListContent(
            reminderStates = it,
            selectedReminderList = selectedReminderList,
            onReminderActions = onReminderActions,
            modifier = modifier
        )
    }
}

@Composable
fun ReminderListContent(
    reminderStates: List<ReminderState>,
    selectedReminderList: ReminderList,
    onReminderActions: (ReminderState) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (reminderStates.isNotEmpty()) {
        ReminderList(
            reminderStates = reminderStates,
            onReminderActions = onReminderActions,
            modifier = modifier,
        )
    } else {
        val emptyStateModifier = modifier.fillMaxSize()

        when (selectedReminderList) {
            ReminderList.OVERDUE -> EmptyStateOverdueReminders(modifier = emptyStateModifier)
            ReminderList.SCHEDULED -> EmptyStateScheduledReminders(modifier = emptyStateModifier)
            ReminderList.COMPLETED -> EmptyStateCompletedReminders(modifier = emptyStateModifier)
        }
    }
}

@Composable
fun ReminderList(
    reminderStates: List<ReminderState>,
    onReminderActions: (ReminderState) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.margin_normal)),
        contentPadding = PaddingValues(
            start = dimensionResource(R.dimen.margin_normal),
            top = dimensionResource(R.dimen.margin_normal),
            end = dimensionResource(R.dimen.margin_normal),
            bottom = dimensionResource(R.dimen.margin_huge)
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        items(reminderStates) { reminderState ->
            ReminderListCard(
                reminderState = reminderState,
                onReminderActions = onReminderActions
            )
        }
    }
}

@Composable
fun ReminderListCard(
    reminderState: ReminderState,
    onReminderActions: (ReminderState) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.margin_normal)),
            modifier = modifier
                .padding(dimensionResource(R.dimen.margin_normal))
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colors.primary,
                ) {
                    Text(
                        text = reminderState.name,
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onPrimary,
                        modifier = Modifier.padding(
                            vertical = 1.dp,
                            horizontal = 8.dp,
                        )
                    )
                }

                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    tint = SubtitleGrey,
                    contentDescription = stringResource(R.string.cd_reminder_actions),
                    modifier = Modifier.clickable { onReminderActions(reminderState) }
                )
            }

            Row {
                ReminderListCardDetail(
                    icon = Icons.Rounded.CalendarToday,
                    text = reminderState.date,
                    contentDescription = stringResource(R.string.cd_details_date),
                    modifier = Modifier.weight(1f)
                )

                ReminderListCardDetail(
                    icon = Icons.Rounded.Schedule,
                    text = reminderState.time.toString(),
                    contentDescription = stringResource(R.string.cd_details_time),
                    modifier = Modifier.weight(1f)
                )
            }

            if (reminderState.isNotificationSent || reminderState.isRepeatReminder) {
                Row {
                    if (reminderState.isNotificationSent) {
                        val notificationText = if (reminderState.isNotificationSent) {
                            stringResource(R.string.reminder_card_notifications_on)
                        } else {
                            stringResource(R.string.reminder_card_notifications_off)
                        }

                        ReminderListCardDetail(
                            icon = Icons.Rounded.NotificationsNone,
                            text = notificationText,
                            contentDescription = stringResource(R.string.cd_details_notification),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (reminderState.isRepeatReminder) {
                        ReminderListCardDetail(
                            icon = Icons.Rounded.Refresh,
                            text = stringResource(
                                R.string.reminder_details_repeat_interval,
                                reminderState.repeatAmount,
                                reminderState.repeatUnit
                            ),
                            contentDescription = stringResource(R.string.cd_details_repeat_interval),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            reminderState.notes?.let { notes ->
                ReminderListCardDetail(
                    icon = Icons.Rounded.Notes,
                    text = notes,
                    contentDescription = stringResource(R.string.cd_details_notes)
                )
            }
        }
    }
}

@Composable
fun ReminderListCardDetail(
    icon: ImageVector,
    text: String,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = SubtitleGrey,
        )

        Spacer(Modifier.width(dimensionResource(R.dimen.margin_tiny)))

        Text(
            text = text,
            style = MaterialTheme.typography.subtitle2,
            color = MaterialTheme.colors.onBackground
        )
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ReminderListContentPreview() {
    RemindMeTheme {
        val reminderStates = PreviewData.previewReminderStates

        ReminderListContent(
            reminderStates = reminderStates,
            selectedReminderList = ReminderList.SCHEDULED,
            onReminderActions = {},
        )
    }
}
