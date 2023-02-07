package dev.shorthouse.remindme.util.enums

import androidx.annotation.StringRes
import dev.shorthouse.remindme.R

enum class ReminderListOrder(@StringRes val displayNameStringId: Int) {
    EARLIEST_DATE_FIRST(R.string.sort_dialog_option_date_earliest),
    LATEST_DATE_FIRST(R.string.sort_dialog_option_date_latest)
}
