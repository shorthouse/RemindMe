package dev.shorthouse.remindme.utilities

import java.time.Duration

fun Duration.toYearPart(): Long {
    return this.toDays().div(DAYS_IN_YEAR)
}

fun Duration.toDayPart(): Long {
    return this.toDays().mod(DAYS_IN_YEAR).toLong()
}

fun Duration.toHourPart(): Long {
    return this.toHours().mod(HOURS_IN_DAY).toLong()
}
