package dev.shorthouse.remindme.utilities

import android.view.View
import androidx.databinding.BindingAdapter
import dev.shorthouse.remindme.model.RepeatInterval

@BindingAdapter("showIfRepeatReminder")
fun showIfRepeatReminder(view: View, repeatInterval: RepeatInterval?) {
    view.visibility = if (repeatInterval != null) View.VISIBLE else View.GONE
}

@BindingAdapter("showIfNotificationEnabled")
fun showIfNotificationEnabled(view: View, isNotificationEnabled: Boolean) {
    view.visibility = if (isNotificationEnabled) View.VISIBLE else View.GONE
}
