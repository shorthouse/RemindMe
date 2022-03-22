package dev.shorthouse.remindme.utilities

import android.view.View
import androidx.databinding.BindingAdapter
import com.google.android.material.textview.MaterialTextView
import dev.shorthouse.remindme.R
import java.time.Duration
import java.time.Instant
import java.time.ZonedDateTime
import java.util.*

@BindingAdapter("app:showIfRepeatReminder")
fun showIfRepeatReminder(view: View, repeatInterval: Long?) {
    view.visibility = if (repeatInterval != null) View.VISIBLE else View.GONE
}

@BindingAdapter("app:hideIfNoNotes")
fun hideIfNoNotes(view: View, notes: String?) {
    view.visibility = if (notes == null) View.GONE else View.VISIBLE
}

@BindingAdapter("app:showIfRepeatChecked")
fun showIfRepeatChecked(view: View, isRepeatChecked: Boolean) {
    view.visibility = if (isRepeatChecked) View.VISIBLE else View.GONE
}

@BindingAdapter("reminderStartDateTime", "reminderRepeatInterval")
fun elapsedIntervals(view: MaterialTextView, startDateTime: ZonedDateTime, repeatInterval: Long?) {
    if (repeatInterval == null) return

    val timeStartEpochToNow = Instant.now().epochSecond.minus(startDateTime.toEpochSecond())
    val numElapsedIntervals = timeStartEpochToNow.div(repeatInterval).plus(1)

    if (numElapsedIntervals > 1) view.text = numElapsedIntervals.toString()
}

@BindingAdapter("app:formattedRepeatInterval")
fun formatRepeatInterval(view: MaterialTextView, repeatInterval: Long?) {
    if (repeatInterval == null) return

    val period = Duration.ofSeconds(repeatInterval)
    val totalDays = period.toDays()

    val years = totalDays.div(DAYS_IN_YEAR)
    val days = totalDays.mod(DAYS_IN_YEAR)
    val hours = period.minusDays(totalDays).toHours()

    val formattedRepeatInterval = StringJoiner(", ")

    if (years > 0) {
        formattedRepeatInterval.add(
            view.resources.getQuantityString(
                R.plurals.interval_years,
                years.toInt(),
                years
            )
        )
    }
    if (days > 0) {
        formattedRepeatInterval.add(
            view.resources.getQuantityString(
                R.plurals.interval_days,
                days,
                days
            )
        )
    }
    if (hours > 0) {
        formattedRepeatInterval.add(
            view.resources.getQuantityString(
                R.plurals.interval_hours,
                hours.toInt(),
                hours
            )
        )
    }

    view.text = formattedRepeatInterval.toString()
}







