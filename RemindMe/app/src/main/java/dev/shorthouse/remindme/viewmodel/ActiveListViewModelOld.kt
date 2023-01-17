package dev.shorthouse.remindme.viewmodel

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.utilities.DAYS_IN_WEEK
import dev.shorthouse.remindme.utilities.enums.ReminderSortOrder
import dev.shorthouse.remindme.utilities.floor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit
import kotlin.time.times
import kotlin.time.toDuration

@HiltViewModel
class ActiveListViewModelOld @Inject constructor(
    val repository: ReminderRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    private val currentAllReminders = repository
        .getAllReminders()
        .asLiveData()

    @VisibleForTesting
    val currentTime = MutableLiveData(ZonedDateTime.now())

    fun getReminders(
        currentSort: MutableLiveData<ReminderSortOrder>,
        currentFilter: MutableLiveData<String>
    ): LiveData<List<Reminder>> {
        val remindersListData = MediatorLiveData<List<Reminder>>()

        remindersListData.addSource(currentAllReminders) {
            remindersListData.value = getRemindersListData(currentSort, currentFilter)
        }
        remindersListData.addSource(currentSort) {
            remindersListData.value = getRemindersListData(currentSort, currentFilter)
        }
        remindersListData.addSource(currentFilter) {
            remindersListData.value = getRemindersListData(currentSort, currentFilter)
        }
        remindersListData.addSource(currentTime) {
            remindersListData.value = getRemindersListData(currentSort, currentFilter)
        }

        return remindersListData
    }

    private fun getRemindersListData(
        currentSort: MutableLiveData<ReminderSortOrder>,
        currentFilter: MutableLiveData<String>
    ): List<Reminder>? {
        val allReminders = currentAllReminders.value
        val time = currentTime.value
        val sort = currentSort.value
        val filter = currentFilter.value

        if (allReminders == null || time == null || sort == null || filter == null) return null

        val activeReminders = allReminders.filter { reminder ->
            reminder.startDateTime.isBefore(time)
        }

        val activeSortedReminders = when (sort) {
            ReminderSortOrder.EARLIEST_DATE_FIRST -> activeReminders.sortedBy { it.startDateTime }
            else -> activeReminders.sortedByDescending { it.startDateTime }
        }

        return if (filter.isBlank()) {
            activeSortedReminders
        } else {
            activeSortedReminders.filter { reminder ->
                reminder.name.contains(filter, true)
            }
        }
    }

    fun updateDoneReminder(reminder: Reminder) {
        viewModelScope.launch(ioDispatcher) {
            delay(150.milliseconds)
            repository.updateReminder(getUpdatedDoneReminder(reminder))
        }
    }

    @VisibleForTesting
    fun getUpdatedDoneReminder(reminder: Reminder): Reminder {
        return Reminder(
            id = reminder.id,
            name = reminder.name,
            startDateTime = getUpdatedStartDateTime(reminder),
            repeatInterval = reminder.repeatInterval,
            notes = reminder.notes,
            isComplete = !reminder.isRepeatReminder(),
            isNotificationSent = reminder.isNotificationSent,
        )
    }

    private fun getUpdatedStartDateTime(reminder: Reminder): ZonedDateTime {
        val repeatInterval = reminder.repeatInterval ?: return reminder.startDateTime

        val repeatDuration = when (repeatInterval.unit) {
            ChronoUnit.DAYS -> repeatInterval.amount.days
            else -> (repeatInterval.amount * DAYS_IN_WEEK).days
        }

        val secondsToNewStartDateTime = ZonedDateTime.now()
            .toEpochSecond()
            .minus(reminder.startDateTime.toEpochSecond())
            .toDuration(DurationUnit.SECONDS)
            .div(repeatDuration)
            .floor()
            .inc()
            .times(repeatDuration)
            .inWholeSeconds

        return reminder.startDateTime.plusSeconds(secondsToNewStartDateTime)
    }

    fun undoDoneReminder(reminder: Reminder) {
        viewModelScope.launch(ioDispatcher) {
            repository.updateReminder(reminder)
        }
    }

    fun updateCurrentTime() {
        currentTime.value = ZonedDateTime.now()
    }

    fun getMillisUntilNextMinute(now: LocalDateTime = LocalDateTime.now()): Long {
        val nextMinute = now.plusMinutes(1).truncatedTo(ChronoUnit.MINUTES)

        return Duration.between(now, nextMinute).toMillis()
    }
}