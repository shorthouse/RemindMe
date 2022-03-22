package dev.shorthouse.remindme.viewmodel

import androidx.lifecycle.*
import dev.shorthouse.remindme.data.ReminderDao
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
        return reminderDao.getActiveNonArchivedReminders(ZonedDateTime.now()).asLiveData()
    }

    fun updateDoneReminder(
        id: Long,
        name: String,
        startDateTime: ZonedDateTime,
        repeatInterval: Pair<Int, ChronoUnit>?,
        notes: String?,
        isNotificationSent: Boolean,
    ) {
        when (repeatInterval) {
            null -> updateDoneSingleReminder(
                id,
                name,
                startDateTime,
                repeatInterval,
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
        repeatInterval: Pair<Int, ChronoUnit>?,
        notes: String?,
        isNotificationSent: Boolean,
    ) {
        val reminder = Reminder(
            id = id,
            name = name,
            startDateTime = startDateTime,
            repeatInterval = repeatInterval,
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
        repeatInterval: Pair<Int, ChronoUnit>,
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
        repeatInterval: Pair<Int, ChronoUnit>,
    ): ZonedDateTime {
        val intervalTimeValue = repeatInterval.first.toLong()
        val intervalTimeUnit = repeatInterval.second
        val period = Period.between(startDateTime.toLocalDate(), LocalDate.now())

        return when (intervalTimeUnit) {
            ChronoUnit.DAYS -> {
                val passedDays = period.days
                val passedIntervals = passedDays.div(intervalTimeValue)
                val nextInterval = passedIntervals.plus(ONE_INTERVAL)
                val daysUntilNextStart = intervalTimeValue * nextInterval
                startDateTime.plusDays(daysUntilNextStart)
            }
            ChronoUnit.WEEKS -> {
                val passedWeeks = period.days.div(DAYS_IN_WEEK)
                val passedIntervals = passedWeeks.div(intervalTimeValue)
                val nextInterval = passedIntervals.plus(ONE_INTERVAL)
                val weeksUntilNextStart = intervalTimeValue * nextInterval
                startDateTime.plusWeeks(weeksUntilNextStart)
            }
            ChronoUnit.MONTHS -> {
                val passedMonths = period.months
                val passedIntervals = passedMonths.div(intervalTimeValue)
                val nextInterval = passedIntervals.plus(ONE_INTERVAL)
                val monthsUntilNextStart = intervalTimeValue * nextInterval
                startDateTime.plusMonths(monthsUntilNextStart)
            }
            else -> {
                val passedYears = period.years
                val passedIntervals = passedYears.div(intervalTimeValue)
                val nextInterval = passedIntervals.plus(ONE_INTERVAL)
                val yearsUntilNextStart = intervalTimeValue * nextInterval
                startDateTime.plusYears(yearsUntilNextStart)
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