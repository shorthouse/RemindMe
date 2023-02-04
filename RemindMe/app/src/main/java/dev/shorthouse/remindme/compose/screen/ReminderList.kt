package dev.shorthouse.remindme.compose.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.hilt.navigation.compose.hiltViewModel
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.component.EmptyStateCompletedReminders
import dev.shorthouse.remindme.compose.component.EmptyStateOverdueReminders
import dev.shorthouse.remindme.compose.component.EmptyStateScheduledReminders
import dev.shorthouse.remindme.compose.component.ReminderListCard
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.utilities.enums.ReminderList
import dev.shorthouse.remindme.utilities.enums.ReminderSortOrder
import dev.shorthouse.remindme.viewmodel.ListViewModel

@Composable
fun ReminderListScreen(
    selectedReminderList: ReminderList,
    selectedReminderSortOrder: ReminderSortOrder,
    onReminderActions: (ReminderState) -> Unit,
    modifier: Modifier = Modifier
) {
    val reminderListViewModel: ListViewModel = hiltViewModel()

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
        )
    } else {
        val emptyStateModifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface)

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
            start = dimensionResource(R.dimen.margin_tiny),
            top = dimensionResource(R.dimen.margin_small),
            end = dimensionResource(R.dimen.margin_tiny),
            bottom = dimensionResource(R.dimen.margin_bottom_bar)
        ),
        modifier = modifier.fillMaxSize()
    ) {
        items(reminderStates) { reminderState ->
            ReminderListCard(
                reminderState = reminderState,
                onReminderActions = onReminderActions
            )
        }
    }
}

//@OptIn(ExperimentalMaterialApi::class)
//@Composable
//fun ReminderListCard(
//    reminderState: ReminderState,
//    onReminderActions: (ReminderState) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Card(
//        onClick = { onReminderActions(reminderState) },
//        modifier = modifier.fillMaxWidth(),
//        shape = RoundedCornerShape(8.dp)
//    ) {
//        Column(modifier = modifier.padding(dimensionResource(R.dimen.margin_normal))) {
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Text(
//                    text = reminderState.name,
//                    style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Medium),
//                    color = MaterialTheme.colors.onSurface,
//                )
//
//                Spacer(Modifier.weight(1f))
//
//                Surface(
//                    shape = RoundedCornerShape(8.dp),
//                    color = Red,
//                ) {
//                    Text(
//                        text = "Overdue",
//                        style = MaterialTheme.typography.subtitle2,
//                        color = MaterialTheme.colors.onPrimary,
//                        modifier = Modifier.padding(
//                            vertical = 1.dp,
//                            horizontal = 8.dp,
//                        )
//                    )
//                }
//            }
//
//            Text(
//                text = "${reminderState.date} • ${reminderState.time}",
//                style = MaterialTheme.typography.subtitle2
//            )
//
//            Spacer(Modifier.height(8.dp))
//
//            if (reminderState.isNotificationSent || reminderState.isRepeatReminder || reminderState.notes != null) {
//                Divider(
//                    color = SubtitleGrey,
//                    thickness = 1.dp
//                )
//                Spacer(Modifier.height(12.dp))
//            }
//
//            if (reminderState.isRepeatReminder) {
//                ReminderListCardDetail(
//                    icon = Icons.Rounded.Refresh,
//                    text = stringResource(
//                        R.string.reminder_details_repeat_interval,
//                        reminderState.repeatAmount,
//                        reminderState.repeatUnit.lowercase()
//                    ),
//                    contentDescription = stringResource(R.string.cd_details_repeat_interval),
//                    modifier = Modifier.weight(1f)
//                )
//            }
//
//            if (reminderState.isNotificationSent) {
//                val notificationText = if (reminderState.isNotificationSent) {
//                    stringResource(R.string.reminder_card_notifications_on)
//                } else {
//                    stringResource(R.string.reminder_card_notifications_off)
//                }
//
//                ReminderListCardDetail(
//                    icon = Icons.Rounded.NotificationsNone,
//                    text = notificationText,
//                    contentDescription = stringResource(R.string.cd_details_notification),
//                    modifier = Modifier.weight(1f)
//                )
//            }
//
//            reminderState.notes?.let { notes ->
//                Spacer(Modifier.height(8.dp))
//
//                ReminderListCardDetail(
//                    icon = Icons.Rounded.Notes,
//                    text = notes,
//                    contentDescription = stringResource(R.string.cd_details_notes)
//                )
//            }
//        }
//    }
//}

//@OptIn(ExperimentalMaterialApi::class)
//@Composable
//fun ReminderListCardOld(
//    reminderState: ReminderState,
//    onReminderActions: (ReminderState) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Card(
//        onClick = { onReminderActions(reminderState) },
//        modifier = modifier.fillMaxWidth(),
//        shape = RoundedCornerShape(8.dp)
//    ) {
//        Column(
//            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.margin_normal)),
//            modifier = modifier.padding(dimensionResource(R.dimen.margin_normal))
//        ) {
//            Surface(
//                shape = RoundedCornerShape(8.dp),
//                color = MaterialTheme.colors.primary,
//            ) {
//                Text(
//                    text = reminderState.name,
//                    style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Medium),
//                    color = MaterialTheme.colors.onPrimary,
//                    modifier = Modifier.padding(
//                        vertical = 1.dp,
//                        horizontal = 8.dp,
//                    )
//                )
//            }
//
//            Row {
//                ReminderListCardDetail(
//                    icon = Icons.Rounded.CalendarToday,
//                    text = reminderState.date,
//                    contentDescription = stringResource(R.string.cd_details_date),
//                    modifier = Modifier.weight(1f)
//                )
//
//                ReminderListCardDetail(
//                    icon = Icons.Rounded.Schedule,
//                    text = reminderState.time.toString(),
//                    contentDescription = stringResource(R.string.cd_details_time),
//                    modifier = Modifier.weight(1f)
//                )
//            }
//
//            if (reminderState.isNotificationSent || reminderState.isRepeatReminder) {
//                Row {
//                    if (reminderState.isNotificationSent) {
//                        val notificationText = if (reminderState.isNotificationSent) {
//                            stringResource(R.string.reminder_card_notifications_on)
//                        } else {
//                            stringResource(R.string.reminder_card_notifications_off)
//                        }
//
//                        ReminderListCardDetail(
//                            icon = Icons.Rounded.NotificationsNone,
//                            text = notificationText,
//                            contentDescription = stringResource(R.string.cd_details_notification),
//                            modifier = Modifier.weight(1f)
//                        )
//                    }
//
//                    if (reminderState.isRepeatReminder) {
//                        ReminderListCardDetail(
//                            icon = Icons.Rounded.Refresh,
//                            text = stringResource(
//                                R.string.reminder_details_repeat_interval,
//                                reminderState.repeatAmount,
//                                reminderState.repeatUnit.lowercase()
//                            ),
//                            contentDescription = stringResource(R.string.cd_details_repeat_interval),
//                            modifier = Modifier.weight(1f)
//                        )
//                    }
//                }
//            }
//
//            reminderState.notes?.let { notes ->
//                ReminderListCardDetail(
//                    icon = Icons.Rounded.Notes,
//                    text = notes,
//                    contentDescription = stringResource(R.string.cd_details_notes)
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun ReminderListCardDetailOld(
//    icon: ImageVector,
//    text: String,
//    contentDescription: String,
//    modifier: Modifier = Modifier
//) {
//    Row(
//        verticalAlignment = Alignment.CenterVertically,
//        modifier = modifier
//    ) {
//        Icon(
//            imageVector = icon,
//            contentDescription = contentDescription,
//            tint = SubtitleGrey,
//        )
//
//        Spacer(Modifier.width(dimensionResource(R.dimen.margin_tiny)))
//
//        Text(
//            text = text,
//            style = MaterialTheme.typography.subtitle2,
//            color = MaterialTheme.colors.onBackground,
//        )
//    }
//}

//@Preview(name = "Light Mode", showBackground = true)
//@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
//@Composable
//fun ReminderListContentPreview() {
//    RemindMeTheme {
//        val reminderStates = PreviewData.previewReminderStates
//
//        ReminderListContent(
//            reminderStates = reminderStates,
//            selectedReminderList = ReminderList.SCHEDULED,
//            onReminderActions = {},
//        )
//    }
//}
//
//@Preview(name = "Light Mode", showBackground = true)
//@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
//@Composable
//fun ReminderListCardOldPreview() {
//    RemindMeTheme {
//        val reminderState = PreviewData.previewReminderState
//
//        ReminderListCardOld(reminderState, {})
//    }
//}
//
//@Preview(name = "Light Mode", showBackground = true)
//@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
//@Composable
//fun ReminderListCardPreview() {
//    RemindMeTheme {
//        val reminderState = PreviewData.previewReminderState
//
//        ReminderListCard(reminderState, {})
//    }
//}
