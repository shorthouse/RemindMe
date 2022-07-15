package dev.shorthouse.remindme.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.data.RepeatInterval
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.utilities.DAYS_IN_WEEK
import dev.shorthouse.remindme.utilities.ONE_INTERVAL
import dev.shorthouse.remindme.utilities.RemindersSort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class ActiveReminderListViewModel @Inject constructor(
    val repository: ReminderRepository,
) : ViewModel() {

    fun getReminders(
        currentSort: MutableLiveData<RemindersSort>,
        currentFilter: MutableLiveData<String>
    ): LiveData<List<Reminder>> {
        val activeReminders = repository
            .getActiveNonArchivedReminders(ZonedDateTime.now())
            .asLiveData()

        val sortedReminders = MediatorLiveData<List<Reminder>>()

        sortedReminders.addSource(activeReminders) {
            sortedReminders.value = sortFilterReminders(activeReminders, currentSort, currentFilter)
        }
        sortedReminders.addSource(currentSort) {
            sortedReminders.value = sortFilterReminders(activeReminders, currentSort, currentFilter)
        }
        sortedReminders.addSource(currentFilter) {
            sortedReminders.value = sortFilterReminders(activeReminders, currentSort, currentFilter)
        }

        return sortedReminders
    }

    private fun sortFilterReminders(
        activeReminders: LiveData<List<Reminder>>,
        currentSort: MutableLiveData<RemindersSort>,
        currentFilter: MutableLiveData<String>
    ): List<Reminder>? {
        val reminders = activeReminders.value
        val sort = currentSort.value
        val filter = currentFilter.value

        if (reminders == null || sort == null) return null

        val sortedReminders = when (sort) {
            RemindersSort.NEWEST_FIRST -> reminders.sortedByDescending { it.startDateTime }
            else -> reminders.sortedBy { it.startDateTime }
        }

        return if (filter == null || filter.isBlank()) {
            sortedReminders
        } else {
            sortedReminders.filter { reminder -> reminder.name.contains(filter, true) }
        }
    }

    fun undoDoneReminder(reminder: Reminder) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateReminder(reminder)
        }
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