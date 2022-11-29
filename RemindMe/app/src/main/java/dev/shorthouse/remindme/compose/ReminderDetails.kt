package dev.shorthouse.remindme.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.viewmodel.DetailsViewModel

@Composable
fun ReminderDetails(detailsViewModel: DetailsViewModel) {
    val reminder by detailsViewModel.reminder.observeAsState()

    reminder?.let {
        ReminderDetailContent(it)
    }
}

@Composable
fun ReminderDetailContent(reminder: Reminder) {
    TODO("Not yet implemented")
}

