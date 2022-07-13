package dev.shorthouse.remindme.utilities

import android.view.View
import androidx.databinding.BindingAdapter
import com.google.android.material.textview.MaterialTextView
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.data.RepeatInterval
import java.time.temporal.ChronoUnit

@BindingAdapter("showIfRepeatReminder")
fun showIfRepeatReminder(view: View, repeatInterval: RepeatInterval?) {
    view.visibility = if (repeatInterval != null) View.VISIBLE else View.GONE
}

@BindingAdapter("showIfNotesExist")
fun showIfNotesExist(view: View, notes: String?) {
    view.visibility = if (notes != null) View.VISIBLE else View.GONE
}

@BindingAdapter("showIfRepeatChecked")
fun showIfRepeatChecked(view: View, isRepeatChecked: Boolean) {
    view.visibility = if (isRepeatChecked) View.VISIBLE else View.GONE
}

@BindingAdapter("showIfNotificationEnabled")
fun showIfNotificationEnabled(view: View, isNotificationEnabled: Boolean) {
    view.visibility = if (isNotificationEnabled) View.VISIBLE else View.GONE
}

@BindingAdapter("formattedRepeatInterval")
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
