package dev.shorthouse.remindme.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.domain.AddReminderUseCase
import dev.shorthouse.remindme.domain.EditReminderUseCase
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.util.UiText
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class InputViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val addReminderUseCase: AddReminderUseCase,
    private val editReminderUseCase: EditReminderUseCase
) : ViewModel() {
    fun getReminder(reminderId: Long): LiveData<Reminder> {
        return reminderRepository.getReminder(reminderId).asLiveData()
    }

    fun isReminderValid(reminder: Reminder): Boolean {
        return reminder.name.isNotBlank() && reminder.startDateTime.isAfter(ZonedDateTime.now())
    }

    fun getErrorMessage(reminder: Reminder): UiText.StringResource {
        return when {
            reminder.name.isBlank() -> UiText.StringResource(R.string.error_name_empty)
            else -> UiText.StringResource(R.string.error_time_past)
        }
    }

    fun saveReminder(reminder: Reminder) {
        when (reminder.id) {
            0L -> addReminderUseCase(reminder)
            else -> editReminderUseCase(reminder)
        }
    }
}
