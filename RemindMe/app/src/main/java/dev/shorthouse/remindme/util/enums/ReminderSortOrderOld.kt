package dev.shorthouse.remindme.util.enums

import androidx.annotation.StringRes
import dev.shorthouse.remindme.R

enum class ReminderSortOrder(@StringRes val displayNameStringId: Int) {
    BY_EARLIEST_DATE_FIRST(R.string.sort_dialog_option_date_earliest),
    BY_LATEST_DATE_FIRST(R.string.sort_dialog_option_date_latest)
}
