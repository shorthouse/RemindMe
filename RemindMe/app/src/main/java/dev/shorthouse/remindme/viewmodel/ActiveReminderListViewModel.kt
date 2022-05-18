package dev.shorthouse.remindme.viewmodel

import androidx.lifecycle.*
import dev.shorthouse.remindme.data.ReminderDao
import dev.shorthouse.remindme.data.RepeatInterval
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.utilities.DAYS_IN_WEEK
import dev.shorthouse.remindme.utilities.ONE_INTERVAL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class ActiveReminderListViewModel(
    private val reminderDao: ReminderDao
) : ViewModel() {

    fun getActiveReminders(): LiveData<List<Reminder>> {
        return reminderDao.getAllActiveNonArchivedReminders(ZonedDateTime.now()).asLiveData()
    }

    fun updateDoneReminder(
        id: Long,
        name: String,
        startDateTime: ZonedDateTime,
        repeatInterval: RepeatInterval?,
        notes: String?,
        isNotificationSent: Boolean,
    ) {
        when (repeatInterval) {
            null -> updateDoneSingleReminder(
                id,
                name,
                startDateTime,
                notes,
                isNotificationSent,
            )
            else -> updateDoneRepeatReminder(
                id,
                name,
                startDateTime,
                repeatInterval,
                notes,
                isNotificationSent,
            )
        }
    }

    private fun updateDoneSingleReminder(
        id: Long,
        name: String,
        startDateTime: ZonedDateTime,
        notes: String?,
        isNotificationSent: Boolean,
    ) {
        val reminder = Reminder(
            id = id,
            name = name,
            startDateTime = startDateTime,
            repeatInterval = null,
            notes = notes,
            isArchived = true,
            isNotificationSent = isNotificationSent,
        )

        viewModelScope.launch(Dispatchers.IO) {
            reminderDao.update(reminder)
        }
    }

    private fun updateDoneRepeatReminder(
        id: Long,
        name: String,
        startDateTime: ZonedDateTime,
        repeatInterval: RepeatInterval,
        notes: String?,
        isNotificationSent: Boolean,
    ) {
        val reminder = Reminder(
            id = id,
            name = name,
            startDateTime = getUpdatedStartDateTime(startDateTime, repeatInterval),
            repeatInterval = repeatInterval,
            notes = notes,
            isArchived = false,
            isNotificationSent = isNotificationSent,
        )

        viewModelScope.launch(Dispatchers.IO) {
            reminderDao.update(reminder)
        }
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

class ActiveReminderListViewModelFactory(
    private val reminderDao: ReminderDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActiveReminderListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ActiveReminderListViewModel(reminderDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}