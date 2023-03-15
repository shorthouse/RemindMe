package dev.shorthouse.remindme.data.protodatastore

import kotlinx.serialization.Serializable

@Serializable
data class UserPreferences(
    val reminderSortOrder: ReminderSortOrder = ReminderSortOrder.BY_EARLIEST_DATE_FIRST
)

enum class ReminderSortOrder {
    BY_EARLIEST_DATE_FIRST,
    BY_LATEST_DATE_FIRST
}

enum class ReminderFilter {
    OVERDUE,
    SCHEDULED,
    COMPLETED
}
