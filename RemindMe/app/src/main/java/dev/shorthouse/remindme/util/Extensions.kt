package dev.shorthouse.remindme.util

import java.time.temporal.ChronoUnit

fun Double.floor(): Double = kotlin.math.floor(this)

fun String.toChronoUnit(): ChronoUnit? {
    return when {
        ChronoUnit.DAYS.name.contains(this, ignoreCase = true) -> ChronoUnit.DAYS
        ChronoUnit.WEEKS.name.contains(this, ignoreCase = true) -> ChronoUnit.WEEKS
        else -> null
    }
}
