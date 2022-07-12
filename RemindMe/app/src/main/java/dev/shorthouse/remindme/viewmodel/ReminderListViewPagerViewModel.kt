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

class ReminderListViewPagerViewModel : ViewModel() {
    val currentSort = MutableLiveData(RemindersSort.NEWEST_FIRST)

    val sortToMenuItemMap = mapOf(
        RemindersSort.NEWEST_FIRST to R.id.drawer_sort_newest_first,
        RemindersSort.OLDEST_FIRST to R.id.drawer_sort_oldest_first
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