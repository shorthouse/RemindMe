package dev.shorthouse.remindme.viewmodel

import androidx.lifecycle.*
import dev.shorthouse.remindme.data.ReminderDao
import dev.shorthouse.remindme.model.Reminder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZonedDateTime

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
        repeatInterval: Long?,
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
        repeatInterval: Long?,
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
        repeatInterval: Long,
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
        repeatInterval: Long
    ): ZonedDateTime {
        val numElapsedIntervals = Instant.now().epochSecond
            .minus(startDateTime.toEpochSecond())
            .div(repeatInterval)

        val oneInterval = 1
        val secondsToNextInterval = numElapsedIntervals
            .plus(oneInterval)
            .times(repeatInterval)

        return startDateTime.plusSeconds(secondsToNextInterval)
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