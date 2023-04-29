package dev.shorthouse.remindme.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.shorthouse.remindme.util.nextHour
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Entity
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String = "",
    val startDateTime: ZonedDateTime = ZonedDateTime.now().nextHour(),
    val isNotificationSent: Boolean = false,
    val repeatInterval: RepeatInterval? = null,
    val notes: String? = null,
    val isCompleted: Boolean = false
) {
    val formattedDate: String
        get() = this.startDateTime
            .toLocalDate()
            .format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy"))

    val formattedTime: String
        get() = this.startDateTime
            .toLocalTime()
            .toString()

    val isRepeatReminder: Boolean
        get() = this.repeatInterval != null

    val isOverdue: Boolean
        get() = this.startDateTime.isBefore(ZonedDateTime.now())

    fun validated(): Reminder {
        return this.copy(
            name = this.name.trim(),
            notes = this.notes?.trim()?.ifBlank { null }
        )
    }

    fun hasOptionalParts(): Boolean = this.isNotificationSent ||
            this.repeatInterval != null ||
            this.notes != null
}
