package dev.shorthouse.remindme.model

import dev.shorthouse.remindme.R
import java.time.temporal.ChronoUnit

data class RepeatInterval(
    val amount: Long,
    val unit: ChronoUnit
) {
    fun getPluralId(): Int {
        return when (unit) {
            ChronoUnit.DAYS -> R.plurals.repeat_interval_days
            else -> R.plurals.repeat_interval_days
        }
    }
}
