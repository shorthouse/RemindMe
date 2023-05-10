package dev.shorthouse.remindme.data.protodatastore

import androidx.annotation.StringRes
import dev.shorthouse.remindme.R
import kotlinx.serialization.Serializable

@Serializable
data class UserPreferences(
    val reminderFilter: ReminderFilter = ReminderFilter.UPCOMING,
    val reminderSortOrder: ReminderSort = ReminderSort.BY_EARLIEST_DATE_FIRST,
    val themeStyle: ThemeStyle = ThemeStyle.AUTO,
    val isNotificationDefaultOn: Boolean = false
)

enum class ReminderFilter(@StringRes val nameStringId: Int) {
    UPCOMING(R.string.reminder_upcoming),
    OVERDUE(R.string.reminder_overdue),
    COMPLETED(R.string.reminder_completed)
}

enum class ReminderSort(@StringRes val nameStringId: Int) {
    BY_EARLIEST_DATE_FIRST(R.string.date_earliest),
    BY_LATEST_DATE_FIRST(R.string.date_latest),
    BY_ALPHABETICAL_A_TO_Z(R.string.alphabetical_a_to_z),
    BY_ALPHABETICAL_Z_TO_A(R.string.alphabetical_z_to_a)
}

enum class ThemeStyle(@StringRes val nameStringId: Int) {
    LIGHT(R.string.theme_light),
    DARK(R.string.theme_dark),
    AUTO(R.string.theme_auto)
}
