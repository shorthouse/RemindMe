package dev.shorthouse.habitbuilder.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Habit (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,

    val timestamp: Long,

    val notes: String?,
)