package dev.shorthouse.habitbuilder.utilities

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.google.android.material.textview.MaterialTextView
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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







