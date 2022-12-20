package dev.shorthouse.remindme.model

import androidx.room.Entity
import androidx.room.PrimaryKey
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
    fun isRepeatReminder() = this.repeatInterval != null

    fun getFormattedStartDate() = this.startDateTime
        .toLocalDate()
        .format(DateTimeFormatter.ofPattern(DATE_PATTERN))
        .toString()

    fun getFormattedStartTime() = this.startDateTime
        .toLocalTime()
        .toString()
}

data class RepeatInterval(
    val amount: Long,
    val unit: ChronoUnit
) 
