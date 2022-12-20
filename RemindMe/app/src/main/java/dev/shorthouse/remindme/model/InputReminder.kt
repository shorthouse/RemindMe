package dev.shorthouse.remindme.model

import androidx.annotation.PluralsRes
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.utilities.DATE_TIME_INPUT_PATTERN
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

data class InputReminder(
    val id: Long = 0L,
    val name: String,
    val startDate: String,
    val startTime: String,
    val isNotificationSent: Boolean,
    val repeatInterval: InputRepeatInterval?,
    val notes: String?,
    val isComplete: Boolean,
) {
    fun toReminder(): Reminder {
        return Reminder(
            id = this.id,
            name = this.name,
            startDateTime = getStartDateTime(this.startDate, this.startTime),
            repeatInterval = getRepeatInterval(this.repeatInterval),
            notes = getNotes(this.notes),
            isNotificationSent = this.isNotificationSent,
            isComplete = isComplete,
        )
    }

    private fun getStartDateTime(dateText: String, timeText: String): ZonedDateTime {
        return LocalDateTime
            .parse(
                "$dateText $timeText",
                DateTimeFormatter.ofPattern(DATE_TIME_INPUT_PATTERN)
            )
            .atZone(ZoneId.systemDefault())
    }

    private fun getRepeatInterval(repeatInterval: InputRepeatInterval?): RepeatInterval? {
        if (repeatInterval == null) return null

        return repeatInterval.toRepeatInterval()
    }

    private fun getNotes(notes: String?): String? {
        return notes?.trim()?.ifBlank { null }
    }
}

data class InputRepeatInterval(
    var amount: Int,
    var pluralId: Int,
) {
    fun toRepeatInterval(): RepeatInterval {
        return RepeatInterval(
            this.amount.toLong(),
            pluralIdToChronoUnit(pluralId)
        )
    }

    private fun pluralIdToChronoUnit(@PluralsRes pluralId: Int): ChronoUnit {
        return when (pluralId) {
            R.plurals.interval_days -> ChronoUnit.DAYS
            else -> ChronoUnit.WEEKS
        }
    }
}
