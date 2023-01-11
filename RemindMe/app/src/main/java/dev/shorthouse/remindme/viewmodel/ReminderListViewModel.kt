package dev.shorthouse.remindme.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.utilities.ReminderSortOrder
import javax.inject.Inject

@HiltViewModel
class ReminderListViewModel @Inject constructor(
    private val repository: ReminderRepository
) : ViewModel() {
    val allReminders = repository.getAllReminders().asLiveData()
    val activeReminders = repository.getActiveReminders().asLiveData()

    val searchFilter: MutableLiveData<String?> = MutableLiveData("")
    val sortOrder = MutableLiveData(ReminderSortOrder.EARLIEST_DATE_FIRST)

//    val allRemindersFilteredSorted: LiveData<List<Reminder>> =
//        Transformations.switchMap(allReminders) {
//            repository.getAllRemindersFilteredSorted(searchFilter.value, ReminderSortOrder.EARLIEST_DATE_FIRST)
//                .asLiveData()
//        }

    val allRemindersFiltered: LiveData<List<Reminder>>
        get() = Transformations.switchMap(searchFilter) { searchString ->
            val filteredReminders = repository
                .getAllRemindersFilteredSorted(searchString, ReminderSortOrder.EARLIEST_DATE_FIRST)
                .asLiveData()

            filteredReminders
        }

//    val allRemindersFiltered = _searchFilter.switchMap { searchString ->
//        if (searchString.isBlank() || allReminders.value == null) allReminders
//        else allReminders.value.filter { it.name.contains(searchString, true) }
//    }

//    val allRemindersFiltered: LiveData<List<Reminder>>
//        get() = Transformations.switchMap(_searchFilter) { searchString ->
//            val filteredReminders = when {
//                searchString.isBlank() -> allReminders
//                else -> {
//                    Transformations.switchMap(allReminders) { reminders ->
//                        val filteredPlayers = MutableLiveData<List<Reminder>>()
//                        val filteredList = reminders.filter { reminder -> reminder.name.contains(searchString, true) }
//                        filteredPlayers.value = filteredList
//                        filteredPlayers
//                    }
//                }
//            }
//
//            filteredReminders
//        }


}

//@Query("SELECT * FROM mytable WHERE suburb LIKE '%' || :suburb || '%' AND postcode LIKE '%' || :postcode || '%' ")
//fun getFiltered(suburb: String?, postcode: String?): LiveData<List<MyTable>>
//
//players = Transformations.switchMap(playersOriginal) { database.getAllPlayerScoresDesc() }
//
//private var players: LiveData<List<Player>> = playersOriginal
