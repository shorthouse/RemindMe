package dev.shorthouse.remindme.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.abs

fun Double.floor(): Double = kotlin.math.floor(this)

fun ZonedDateTime.nextHour(): ZonedDateTime = this.truncatedTo(ChronoUnit.HOURS).plusHours(1)

// Workaround for https://issuetracker.google.com/issues/186669832
fun Modifier.disableBottomSheetSwipe() = pointerInput(Unit) {
    awaitPointerEventScope {
        while (true) {
            awaitPointerEvent(pass = PointerEventPass.Initial).changes.forEach {
                val offset = it.positionChange()
                if (abs(offset.y) > 0f) {
                    it.consume()
                }
            }
        }
    }
}
