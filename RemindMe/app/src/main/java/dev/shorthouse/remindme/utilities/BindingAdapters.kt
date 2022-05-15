package dev.shorthouse.remindme.utilities

import android.view.View
import androidx.databinding.BindingAdapter
import com.google.android.material.textview.MaterialTextView
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.data.RepeatInterval
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

@BindingAdapter("app:showIfRepeatReminder")
fun showIfRepeatReminder(view: View, repeatInterval: RepeatInterval?) {
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
fun formatRepeatInterval(view: MaterialTextView, repeatInterval: RepeatInterval?) {
    if (repeatInterval == null) return

    val timeValue = repeatInterval.timeValue.toInt()

    when (repeatInterval.timeUnit) {
        ChronoUnit.DAYS -> view.text =
            view.resources.getQuantityString(R.plurals.interval_days, timeValue, timeValue)
        else -> view.text =
            view.resources.getQuantityString(R.plurals.interval_weeks, timeValue, timeValue)
    }
}







