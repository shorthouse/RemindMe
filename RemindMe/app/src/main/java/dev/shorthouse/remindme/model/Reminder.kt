package dev.shorthouse.remindme.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

@Entity
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val startDateTime: ZonedDateTime,
    val isNotificationSent: Boolean,
    val repeatInterval: RepeatInterval?,
    val notes: String?,
    val isCompleted: Boolean
) {
    fun getFormattedStartTime() = this.startDateTime
        .toLocalTime()
        .toString()

    fun isRepeatReminder() = this.repeatInterval != null
}

data class RepeatInterval(
    val amount: Long,
    val unit: ChronoUnit
)
