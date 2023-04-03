package dev.shorthouse.remindme.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Entity
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String = "",
    val startDateTime: ZonedDateTime = ZonedDateTime.now().truncatedTo(ChronoUnit.MINUTES),
    val isNotificationSent: Boolean = false,
    val repeatInterval: RepeatInterval? = null,
    val notes: String? = null,
    val isCompleted: Boolean = false
) {
    fun getFormattedDate(): String = this.startDateTime
        .toLocalDate()
        .format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy"))

    fun getFormattedTime(): String = this.startDateTime
        .toLocalTime()
        .toString()

    fun isRepeatReminder() = this.repeatInterval != null

    fun isOverdue() = this.startDateTime.isBefore(ZonedDateTime.now())
}
