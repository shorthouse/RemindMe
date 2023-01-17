package dev.shorthouse.remindme.viewmodel

import android.graphics.Color
import androidx.annotation.VisibleForTesting
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.math.MathUtils
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.utilities.SCRIM_LERP_MAX
import dev.shorthouse.remindme.utilities.SCRIM_START_ALPHA
import dev.shorthouse.remindme.utilities.enums.ReminderSortOrder

class ListContainerViewModel : ViewModel() {
    val currentSort = MutableLiveData(ReminderSortOrder.EARLIEST_DATE_FIRST)
    val currentFilter = MutableLiveData("")
    var bottomSheetListSelection = R.id.drawer_active_list

    val sortToMenuItemMap = mapOf(
        ReminderSortOrder.EARLIEST_DATE_FIRST to R.id.drawer_sort_earliest_date_first,
        ReminderSortOrder.LATEST_DATE_FIRST to R.id.drawer_sort_latest_date_first
    )

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val menuItemToSortMap = sortToMenuItemMap.entries.associate { (key, value) ->
        value to key
    }

    fun calculateScrimColor(slideOffset: Float): Int {
        val baseColor = Color.BLACK
        val baseAlpha = SCRIM_START_ALPHA
        val offset = (slideOffset - (-1f)) / (1f - (-1f)) * (1f - 0f) + 0f
        val alpha = MathUtils.lerp(0f, SCRIM_LERP_MAX, offset * baseAlpha).toInt()
        return Color.argb(alpha, baseColor.red, baseColor.green, baseColor.blue)
    }

    fun isItemChanged(itemIdOne: Int, itemIdTwo: Int?): Boolean {
        return itemIdOne != itemIdTwo
    }

    fun setCurrentSort(itemId: Int) {
        menuItemToSortMap[itemId]?.let { sort ->
            currentSort.value = sort
        }
    }

    fun getToolbarTitle(): Int {
        return when (bottomSheetListSelection) {
            R.id.drawer_all_list -> R.string.all_reminders
            else -> R.string.active_reminders
        }
    }
}
