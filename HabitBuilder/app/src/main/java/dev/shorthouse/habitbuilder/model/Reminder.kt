package dev.shorthouse.habitbuilder.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Reminder (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,

    val reminderEpoch: Long,

    val notes: String?,
)