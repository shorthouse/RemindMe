package dev.shorthouse.remindme.model

data class DisplayReminder(
    val id: Long = 0L,
    val name: String,
    val startDate: String,
    val startTime: String,
    val isNotificationSent: Boolean,
    val repeatInterval: DisplayRepeatInterval?,
    val notes: String?,
)
