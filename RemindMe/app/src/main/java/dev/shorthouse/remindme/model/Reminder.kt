package dev.shorthouse.remindme.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.shorthouse.remindme.data.RepeatInterval
import java.time.ZonedDateTime

@Entity
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    val name: String,

    val startDateTime: ZonedDateTime,

    val repeatInterval: RepeatInterval?,

    val notes: String?,

    val isArchived: Boolean,

    val isNotificationSent: Boolean,
)
