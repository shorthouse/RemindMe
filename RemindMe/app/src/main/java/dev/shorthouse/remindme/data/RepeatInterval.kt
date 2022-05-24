package dev.shorthouse.remindme.data

import java.time.temporal.ChronoUnit

data class RepeatInterval(
    val timeValue: Long,
    val timeUnit: ChronoUnit
)
