package dev.shorthouse.remindme.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.utilities.RemindersSort
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class AllReminderListViewModel @Inject constructor(
    val repository: ReminderRepository,
) : ViewModel() {

    fun getSortedReminders(currentSort: MutableLiveData<RemindersSort>): LiveData<List<Reminder>> {
        val allReminders = repository
            .getNonArchivedReminders()
            .asLiveData()

        val sortedReminders = MediatorLiveData<List<Reminder>>()

        sortedReminders.addSource(allReminders) {
            sortedReminders.value = sortReminders(allReminders, currentSort)
        }
        sortedReminders.addSource(currentSort) {
            sortedReminders.value = sortReminders(allReminders, currentSort)
        }

        return sortedReminders
    }

    private fun sortReminders(
        allReminders: LiveData<List<Reminder>>,
        currentSort: MutableLiveData<RemindersSort>
    ): List<Reminder>? {
        val reminders = allReminders.value
        val sort = currentSort.value

        if (reminders == null || sort == null) return null

        return when (sort) {
            RemindersSort.NEWEST_FIRST -> reminders.sortedByDescending { it.startDateTime }
            else -> reminders.sortedBy { it.startDateTime }
        }
    }
}