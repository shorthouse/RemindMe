package dev.shorthouse.remindme.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.utilities.DATE_INPUT_PATTERN
import dev.shorthouse.remindme.utilities.DATE_PATTERN
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Entity
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    val name: String,

    val startDateTime: ZonedDateTime,

    val repeatInterval: RepeatInterval?,

    val notes: String?,

    val isComplete: Boolean,

    val isNotificationSent: Boolean,
) {
    fun createDisplayReminder(): DisplayReminder {
        return DisplayReminder(
            id = this.id,
            name = this.name,
            startDate = getFormattedStartDateDayOfWeek(),
            startTime = getFormattedStartTime(),
            isNotificationSent = this.isNotificationSent,
            repeatInterval = getDisplayRepeatInterval(),
            notes = this.notes
        )
    }

    private fun getDisplayRepeatInterval(): DisplayRepeatInterval? {
        if (repeatInterval == null) return null

        return DisplayRepeatInterval(
            getRepeatIntervalStringId(repeatInterval.timeUnit),
            repeatInterval.timeValue.toInt()
        )
    }

    fun getFormattedStartDate() = this.startDateTime
        .toLocalDate()
        .format(DateTimeFormatter.ofPattern(DATE_PATTERN))
        .toString()

    fun getFormattedStartDateDayOfWeek() = this.startDateTime
        .toLocalDate()
        .format(DateTimeFormatter.ofPattern(DATE_INPUT_PATTERN))
        .toString()

    fun getFormattedStartTime() = this.startDateTime
        .toLocalTime()
        .toString()

    fun isRepeatReminder() = this.repeatInterval != null

    private fun getRepeatIntervalStringId(timeUnit: ChronoUnit): Int {
        return when (timeUnit) {
            ChronoUnit.DAYS -> R.plurals.interval_days
            else -> R.plurals.interval_weeks
        }
    }
}
