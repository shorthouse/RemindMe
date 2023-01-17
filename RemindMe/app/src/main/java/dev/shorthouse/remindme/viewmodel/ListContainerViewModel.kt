package dev.shorthouse.remindme.viewmodel
//
//import androidx.annotation.VisibleForTesting
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import dev.shorthouse.remindme.R
//import dev.shorthouse.remindme.utilities.enums.ReminderSortOrder
//
//class ListContainerViewModel : ViewModel() {
//    val currentSort = MutableLiveData(ReminderSortOrder.EARLIEST_DATE_FIRST)
//    val currentFilter = MutableLiveData("")
//    var bottomSheetListSelection = R.id.drawer_active_list
//
//    val sortToMenuItemMap = mapOf(
//        ReminderSortOrder.EARLIEST_DATE_FIRST to R.id.drawer_sort_earliest_date_first,
//        ReminderSortOrder.LATEST_DATE_FIRST to R.id.drawer_sort_latest_date_first
//    )
//
//    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
//    val menuItemToSortMap = sortToMenuItemMap.entries.associate { (key, value) ->
//        value to key
//    }
//
//    fun setCurrentSort(itemId: Int) {
//        menuItemToSortMap[itemId]?.let { sort ->
//            currentSort.value = sort
//        }
//    }
//}
