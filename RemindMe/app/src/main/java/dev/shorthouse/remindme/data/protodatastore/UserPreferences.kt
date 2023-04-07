package dev.shorthouse.remindme.data.protodatastore

import androidx.annotation.StringRes
import dev.shorthouse.remindme.R
import kotlinx.serialization.Serializable

@Serializable
data class UserPreferences(
    val reminderFilter: ReminderFilter = ReminderFilter.UPCOMING,
    val reminderSortOrder: ReminderSort = ReminderSort.BY_EARLIEST_DATE_FIRST
)

enum class ReminderFilter(@StringRes val nameStringId: Int) {
    OVERDUE(R.string.reminder_overdue),
    UPCOMING(R.string.reminder_upcoming),
    COMPLETED(R.string.reminder_completed)
}

enum class ReminderSort(@StringRes val nameStringId: Int) {
    BY_EARLIEST_DATE_FIRST(R.string.sort_dialog_option_date_earliest),
    BY_LATEST_DATE_FIRST(R.string.sort_dialog_option_date_latest),
    BY_ALPHABETICAL_A_TO_Z(R.string.sort_dialog_option_alphabetical_a_to_z),
    BY_ALPHABETICAL_Z_TO_A(R.string.sort_dialog_option_alphabetical_z_to_a)
}
