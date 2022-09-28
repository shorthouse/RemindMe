package dev.shorthouse.remindme.viewmodel

import android.graphics.Color
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.math.MathUtils
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.utilities.RemindersSort

class ListViewPagerViewModel : ViewModel() {
    val currentSort = MutableLiveData(RemindersSort.EARLIEST_DATE_FIRST)
    val currentFilter = MutableLiveData("")

    val sortToMenuItemMap = mapOf(
        RemindersSort.EARLIEST_DATE_FIRST to R.id.drawer_sort_earliest_date_first,
        RemindersSort.LATEST_DATE_FIRST to R.id.drawer_sort_latest_date_first
    )

    val menuItemToSortMap = sortToMenuItemMap.entries.associate { (key, value) ->
        value to key
    }

    fun getScrimBackgroundColour(slideOffset: Float): Int {
        val baseColor = Color.BLACK
        val baseAlpha = 0.6f
        val offset = (slideOffset - (-1f)) / (1f - (-1f)) * (1f - 0f) + 0f
        val alpha = MathUtils.lerp(0f, 255f, offset * baseAlpha).toInt()
        return Color.argb(alpha, baseColor.red, baseColor.green, baseColor.blue)
    }

}