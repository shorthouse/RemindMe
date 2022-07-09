package dev.shorthouse.remindme.viewmodel

import android.graphics.Color
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.math.MathUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.data.RepeatInterval
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.utilities.DAYS_IN_WEEK
import dev.shorthouse.remindme.utilities.ONE_INTERVAL
import dev.shorthouse.remindme.utilities.RemindersFilter
import dev.shorthouse.remindme.utilities.RemindersSort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class ReminderListViewModel @Inject constructor(
    val repository: ReminderRepository,
) : ViewModel() {

    private val activeReminders = repository
        .getActiveNonArchivedReminders(ZonedDateTime.now())
        .asLiveData()

    private val allReminders = repository
        .getNonArchivedReminders()
        .asLiveData()

    val remindersList = MediatorLiveData<List<Reminder>>()

    var currentFilter = RemindersFilter.ACTIVE_REMINDERS
    var currentSort = RemindersSort.NEWEST_FIRST

    init {
        remindersList.addSource(activeReminders) { updateReminderList() }
        remindersList.addSource(allReminders) { updateReminderList() }
    }

    fun filterReminderList(filter: RemindersFilter) {
        currentFilter = filter
        updateReminderList()
    }

    fun sortReminderList(sort: RemindersSort) {
        currentSort = sort
        updateReminderList()
    }

    private fun updateReminderList() {
        val remindersFiltered = when (currentFilter) {
            RemindersFilter.ACTIVE_REMINDERS -> activeReminders.value
            else -> allReminders.value
        }

        val remindersFilteredSorted = when (currentSort) {
            RemindersSort.NEWEST_FIRST -> remindersFiltered?.sortedByDescending { it.startDateTime }
            else -> remindersFiltered?.sortedBy { it.startDateTime }
        }

        remindersList.value = remindersFilteredSorted
    }

    fun getScrimBackgroundColour(slideOffset: Float): Int {
        val baseColor = Color.BLACK
        val baseAlpha = 0.6f
        val offset = (slideOffset - (-1f)) / (1f - (-1f)) * (1f - 0f) + 0f
        val alpha = MathUtils.lerp(0f, 255f, offset * baseAlpha).toInt()
        return Color.argb(alpha, baseColor.red, baseColor.green, baseColor.blue)
    }

    fun updateDoneReminder(reminder: Reminder) {
        val updatedDoneReminder = if (reminder.isRepeatReminder()) {
            getUpdatedRepeatReminder(
                reminder.id,
                reminder.name,
                reminder.startDateTime,
                reminder.repeatInterval!!,
                reminder.notes,
                reminder.isNotificationSent
            )
        } else {
            getCompletedOneOffReminder(
                reminder.id,
                reminder.name,
                reminder.startDateTime,
                reminder.repeatInterval,
                reminder.notes,
                reminder.isNotificationSent
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            repository.updateReminder(updatedDoneReminder)
        }
    }

    private fun getCompletedOneOffReminder(
        id: Long,
        name: String,
        startDateTime: ZonedDateTime,
        repeatInterval: RepeatInterval?,
        notes: String?,
        isNotificationSent: Boolean,
    ): Reminder {
        return Reminder(
            id = id,
            name = name,
            startDateTime = startDateTime,
            repeatInterval = repeatInterval,
            notes = notes,
            isArchived = true,
            isNotificationSent = isNotificationSent,
        )
    }

    private fun getUpdatedRepeatReminder(
        id: Long,
        name: String,
        startDateTime: ZonedDateTime,
        repeatInterval: RepeatInterval,
        notes: String?,
        isNotificationSent: Boolean,
    ): Reminder {
        return Reminder(
            id = id,
            name = name,
            startDateTime = getUpdatedStartDateTime(startDateTime, repeatInterval),
            repeatInterval = repeatInterval,
            notes = notes,
            isArchived = false,
            isNotificationSent = isNotificationSent,
        )
    }

    private fun getUpdatedStartDateTime(
        startDateTime: ZonedDateTime,
        repeatInterval: RepeatInterval,
    ): ZonedDateTime {
        val period = Period.between(startDateTime.toLocalDate(), LocalDate.now())
        val timeValue = repeatInterval.timeValue

        return when (repeatInterval.timeUnit) {
            ChronoUnit.DAYS -> {
                val passedDays = period.days
                val passedIntervals = passedDays.div(timeValue)
                val nextInterval = passedIntervals.plus(ONE_INTERVAL)
                val daysUntilNextStart = timeValue * nextInterval
                startDateTime.plusDays(daysUntilNextStart)
            }
            else -> {
                val passedWeeks = period.days.div(DAYS_IN_WEEK)
                val passedIntervals = passedWeeks.div(timeValue)
                val nextInterval = passedIntervals.plus(ONE_INTERVAL)
                val weeksUntilNextStart = timeValue * nextInterval
                startDateTime.plusWeeks(weeksUntilNextStart)
            }
        }
    }
}
