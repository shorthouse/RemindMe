package dev.shorthouse.remindme.model

import java.time.temporal.ChronoUnit

data class RepeatInterval(
    val amount: Long,
    val unit: ChronoUnit
)
