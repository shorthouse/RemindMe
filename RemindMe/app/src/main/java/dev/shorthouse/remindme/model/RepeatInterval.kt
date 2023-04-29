package dev.shorthouse.remindme.model

import java.time.temporal.ChronoUnit

data class RepeatInterval(
    val amount: Int,
    val unit: ChronoUnit
)
