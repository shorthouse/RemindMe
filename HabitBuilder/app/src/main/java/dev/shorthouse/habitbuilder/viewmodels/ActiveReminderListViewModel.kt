package dev.shorthouse.habitbuilder.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dev.shorthouse.habitbuilder.data.ReminderDao
import dev.shorthouse.habitbuilder.model.Reminder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant

class ActiveReminderListViewModel(
    private val reminderDao: ReminderDao
) : ViewModel() {
    val activeReminders = reminderDao.getActiveReminders(Instant.now().epochSecond).asLiveData()

    fun updateDoneReminder(
        id: Long,
        name: String,
        startEpoch: Long,
        repeatInterval: Long?,
        notes: String?,
    ) {
        when (repeatInterval) {
            null -> updateDoneSingleReminder(
                id,
                name,
                startEpoch,
                repeatInterval,
                notes,
            )
            else -> updateDoneRepeatReminder(
                id,
                name,
                startEpoch,
                repeatInterval,
                notes,
            )
        }
    }

    private fun updateDoneSingleReminder(
        id: Long,
        name: String,
        startEpoch: Long,
        repeatInterval: Long?,
        notes: String?,
    ) {
        val reminder = Reminder(
            id = id,
            name = name,
            startEpoch = startEpoch,
            repeatInterval = repeatInterval,
            notes = notes,
            isArchived = true,
        )

        viewModelScope.launch(Dispatchers.IO) {
            reminderDao.update(reminder)
        }
    }

    private fun updateDoneRepeatReminder(
        id: Long,
        name: String,
        startEpoch: Long,
        repeatInterval: Long,
        notes: String?,
    ) {
        val reminder = Reminder(
            id = id,
            name = name,
            startEpoch = getUpdatedStartEpoch(startEpoch, repeatInterval),
            repeatInterval = repeatInterval,
            notes = notes,
            isArchived = false,
        )

        viewModelScope.launch(Dispatchers.IO) {
            reminderDao.update(reminder)
        }
    }

    private fun getUpdatedStartEpoch(startEpoch: Long, repeatInterval: Long): Long {
        val timeStartEpochToNow = Instant.now().epochSecond.minus(startEpoch)
        val numElapsedIntervals = timeStartEpochToNow.div(repeatInterval)
        val nextInterval = numElapsedIntervals.plus(1)
        val timeStartEpochToNextInterval = nextInterval.times(repeatInterval)
        return startEpoch.plus(timeStartEpochToNextInterval)
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