package dev.shorthouse.remindme.utilities

import android.view.View
import androidx.databinding.BindingAdapter
import com.google.android.material.textview.MaterialTextView
import dev.shorthouse.remindme.R
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@BindingAdapter("app:hideIfSingleReminder")
fun hideIfSingleReminder(view: View, repeatInterval: Long?) {
    view.visibility = if (repeatInterval == null) View.GONE else View.VISIBLE
}

@BindingAdapter("app:hideIfNoNotes")
fun hideIfNoNotes(view: View, notes: String?) {
    view.visibility = if (notes == null) View.GONE else View.VISIBLE
}

@BindingAdapter("app:showIfRepeatReminder")
fun hideIfSingleReminder(view: View, isRepeatReminder: Boolean) {
    view.visibility = if (isRepeatReminder) View.VISIBLE else View.GONE
}

@BindingAdapter("app:formattedTime")
fun formattedTime(view: MaterialTextView, reminderStartEpoch: Long) {
    view.text = LocalDateTime.ofInstant(
        Instant.ofEpochSecond(reminderStartEpoch),
        ZoneId.systemDefault()
    )
        .toLocalTime()
        .toString()
}

@BindingAdapter("app:formattedDate")
fun formattedDate(view: MaterialTextView, reminderStartEpoch: Long) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

    view.text = Instant.ofEpochSecond(reminderStartEpoch)
        .atZone(ZoneId.systemDefault())
        .format(dateFormatter)

}

@BindingAdapter("app:formattedRepeatInterval")
fun formatRepeatInterval(view: MaterialTextView, repeatInterval: Long?) {
    if (repeatInterval == null) return

    val period = Duration.ofSeconds(repeatInterval)
    val totalDays = period.toDays()

    val DAYS_IN_YEAR = 365
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







