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

        val remindersListData = MediatorLiveData<List<Reminder>>()

        remindersListData.addSource(allReminders) {
            remindersListData.value = sortFilterReminders(allReminders, currentSort, currentFilter)
        }
        remindersListData.addSource(currentSort) {
            remindersListData.value = sortFilterReminders(allReminders, currentSort, currentFilter)
        }
        remindersListData.addSource(currentFilter) {
            remindersListData.value = sortFilterReminders(allReminders, currentSort, currentFilter)
        }

        return remindersListData
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
            RemindersSort.EARLIEST_DATE_FIRST -> reminders.sortedBy { it.startDateTime }
            else -> reminders.sortedByDescending { it.startDateTime }
        }

        return if (filter == null || filter.isBlank()) {
            sortedReminders
        } else {
            sortedReminders.filter { reminder -> reminder.name.contains(filter, true) }
        }
    }
}