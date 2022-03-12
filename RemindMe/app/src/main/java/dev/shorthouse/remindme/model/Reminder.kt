package dev.shorthouse.remindme.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,

    val startEpoch: Long,

    val repeatInterval: Long?,

    val notes: String?,

    val isArchived: Boolean,
)
