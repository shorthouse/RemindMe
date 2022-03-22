package dev.shorthouse.remindme.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

@Entity
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,

    val startDateTime: ZonedDateTime,

    val repeatInterval: Pair<Long, ChronoUnit>,
    //val repeatInterval: Long?,

    val notes: String?,

    val isArchived: Boolean,

    val isNotificationSent: Boolean,
)
