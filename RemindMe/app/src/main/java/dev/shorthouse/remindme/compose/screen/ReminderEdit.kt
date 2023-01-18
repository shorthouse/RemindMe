package dev.shorthouse.remindme.compose.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.viewmodel.EditViewModel
import dev.shorthouse.remindme.viewmodel.InputViewModel

@Destination
@Composable
fun ReminderEditScreen(
    reminderId: Long,
    inputViewModel: InputViewModel = hiltViewModel(),
    editViewModel: EditViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val reminder by editViewModel.getReminder(reminderId).observeAsState()

    reminder?.let {
        ReminderInputScreen(
            reminderState = ReminderState(it),
            inputViewModel = inputViewModel,
            topBarTitle = stringResource(R.string.top_bar_title_edit_reminder),
            navigator = navigator
        )
    }
}
