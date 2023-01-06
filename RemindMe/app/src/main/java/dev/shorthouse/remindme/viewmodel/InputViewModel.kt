package dev.shorthouse.remindme.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.utilities.UiText
import java.time.ZonedDateTime

@HiltViewModel
class InputViewModel : ViewModel() {
    fun isReminderValid(reminder: Reminder): Boolean {
        return reminder.name.isNotBlank() &&
                reminder.startDateTime.isAfter(ZonedDateTime.now())
    }

    fun getErrorMessage(reminder: Reminder): UiText.StringResource {
        return when {
            reminder.name.isBlank() -> UiText.StringResource(R.string.error_name_empty)
            else -> UiText.StringResource(R.string.error_time_past)
        }
    }
}
