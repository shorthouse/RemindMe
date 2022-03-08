package dev.shorthouse.habitbuilder.utilities

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter

@BindingAdapter("app:hideIfSingleReminder")
fun hideIfSingleReminder(view: View, repeatInterval: Long?) {
    view.visibility = if (repeatInterval == null) View.GONE else View.VISIBLE
}

@BindingAdapter("app:hideIfNoNotes")
fun hideIfNoNotes(view: View, notes: String?) {
    view.visibility = if (notes == null) View.GONE else View.VISIBLE
}

@BindingAdapter("app:showIfRepeatReminder")
fun hideIfSingleReminder(view: View, isRepeatReminder:Boolean) {
    view.visibility = if (isRepeatReminder) View.VISIBLE else View.GONE
}
