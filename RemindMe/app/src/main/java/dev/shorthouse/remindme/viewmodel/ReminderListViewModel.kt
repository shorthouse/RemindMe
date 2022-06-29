package dev.shorthouse.remindme.viewmodel

import android.graphics.Color
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.android.material.math.MathUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.data.ReminderRepository
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class ReminderListViewModel @Inject constructor(
    repository: ReminderRepository,
) : ViewModel() {
    val activeReminders = repository.getActiveNonArchivedReminders(ZonedDateTime.now()).asLiveData()

    val allReminders = repository.getNonArchivedReminders().asLiveData()

    fun getScrimBackgroundColour(slideOffset: Float): Int {
        val baseColor = Color.BLACK
        val baseAlpha = 0.6f
        val offset = (slideOffset - (-1f)) / (1f - (-1f)) * (1f - 0f) + 0f
        val alpha = MathUtils.lerp(0f, 255f, offset * baseAlpha).toInt()
        return Color.argb(alpha, baseColor.red, baseColor.green, baseColor.blue)
    }
}
