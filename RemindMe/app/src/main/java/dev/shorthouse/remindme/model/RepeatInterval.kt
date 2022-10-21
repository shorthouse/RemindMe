package dev.shorthouse.remindme.model

import java.time.temporal.ChronoUnit

data class RepeatInterval(
    val timeValue: Long,
    val timeUnit: ChronoUnit
)
