package dev.shorthouse.remindme.viewmodel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dev.shorthouse.remindme.utilities.enums.ReminderList
import dev.shorthouse.remindme.utilities.enums.ReminderSortOrder

class ListHomeViewModel : ViewModel() {
    var selectedSortIndex by mutableStateOf(0)

    val selectedReminderSortOrder by derivedStateOf {
        when (selectedSortIndex) {
            0 -> ReminderSortOrder.EARLIEST_DATE_FIRST
            else -> ReminderSortOrder.LATEST_DATE_FIRST
        }
    }

    var selectedNavigateIndex by mutableStateOf(0)

    val selectedReminderList by derivedStateOf {
        when (selectedNavigateIndex) {
            0 -> ReminderList.OVERDUE
            1 -> ReminderList.SCHEDULED
            else -> ReminderList.COMPLETED
        }
    }
}
