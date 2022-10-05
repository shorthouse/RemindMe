package dev.shorthouse.remindme.viewmodel

import android.graphics.Color
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.math.MathUtils
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.utilities.RemindersList
import dev.shorthouse.remindme.utilities.RemindersSort

class ListContainerViewModel : ViewModel() {
    val currentSort = MutableLiveData(RemindersSort.EARLIEST_DATE_FIRST)
    val currentFilter = MutableLiveData("")
    var currentList = RemindersList.ACTIVE_REMINDERS

    val sortToMenuItemMap = mapOf(
        RemindersSort.EARLIEST_DATE_FIRST to R.id.drawer_sort_earliest_date_first,
        RemindersSort.LATEST_DATE_FIRST to R.id.drawer_sort_latest_date_first
    )

    val menuItemToSortMap = sortToMenuItemMap.entries.associate { (key, value) ->
        value to key
    }

    val listToMenuItemMap = mapOf(
        RemindersList.ACTIVE_REMINDERS to R.id.drawer_active_list,
        RemindersList.ALL_REMINDERS to R.id.drawer_all_list,
    )

    val menuItemToListMap = listToMenuItemMap.entries.associate { (key, value) ->
        value to key
    }

    fun calculateScrimColor(slideOffset: Float): Int {
        val baseColor = Color.BLACK
        val baseAlpha = 0.6f
        val offset = (slideOffset - (-1f)) / (1f - (-1f)) * (1f - 0f) + 0f
        val alpha = MathUtils.lerp(0f, 255f, offset * baseAlpha).toInt()
        return Color.argb(alpha, baseColor.red, baseColor.green, baseColor.blue)
    }

    fun isItemChanged(itemIdOne: Int, itemIdTwo: Int?): Boolean {
        return itemIdOne != itemIdTwo
    }

    fun setCurrentList(itemId: Int) {
        menuItemToListMap[itemId]?.let { list ->
            currentList = list
        }
    }

    fun getCurrentListItemId(): Int {
        return listToMenuItemMap[currentList]!!
    }

    fun setCurrentSort(itemId: Int) {
        menuItemToSortMap[itemId]?.let { sort ->
            currentSort.value = sort
        }
    }
}