package dev.shorthouse.remindme.data.protodatastore

import androidx.annotation.StringRes
import dev.shorthouse.remindme.R
import kotlinx.serialization.Serializable

@Serializable
data class UserPreferences(
    val reminderSortOrder: ReminderSort = ReminderSort.BY_EARLIEST_DATE_FIRST,
    val reminderFilters: Set<ReminderFilter> = setOf(ReminderFilter.SCHEDULED)
)

enum class ReminderSort(@StringRes val nameStringId: Int) {
    BY_EARLIEST_DATE_FIRST(R.string.sort_dialog_option_date_earliest),
    BY_LATEST_DATE_FIRST(R.string.sort_dialog_option_date_latest)
}

enum class ReminderFilter(@StringRes val nameStringId: Int) {
    OVERDUE(R.string.overdue),
    SCHEDULED(R.string.scheduled),
    COMPLETED(R.string.completed)
}
