package dev.shorthouse.remindme.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.utilities.RemindersSort
import javax.inject.Inject

@HiltViewModel
class AllReminderListViewModel @Inject constructor(
    val repository: ReminderRepository,
) : ViewModel() {

    fun getReminders(
        currentSort: MutableLiveData<RemindersSort>,
        currentFilter: MutableLiveData<String>
    ): LiveData<List<Reminder>> {
        val allReminders = repository
            .getNonArchivedReminders()
            .asLiveData()

        val sortedReminders = MediatorLiveData<List<Reminder>>()

        sortedReminders.addSource(allReminders) {
            sortedReminders.value = sortFilterReminders(allReminders, currentSort, currentFilter)
        }
        sortedReminders.addSource(currentSort) {
            sortedReminders.value = sortFilterReminders(allReminders, currentSort, currentFilter)
        }
        sortedReminders.addSource(currentFilter) {
            sortedReminders.value = sortFilterReminders(allReminders, currentSort, currentFilter)
        }

        return sortedReminders
    }

    private fun sortFilterReminders(
        allReminders: LiveData<List<Reminder>>,
        currentSort: MutableLiveData<RemindersSort>,
        currentFilter: MutableLiveData<String>
    ): List<Reminder>? {
        val reminders = allReminders.value
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
}