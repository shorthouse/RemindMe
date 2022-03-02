package dev.shorthouse.habitbuilder.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import dev.shorthouse.habitbuilder.data.ReminderDao
import dev.shorthouse.habitbuilder.model.Reminder
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class ReminderDetailsViewModel(private val reminderDao: ReminderDao
) : ViewModel() {

    fun getReminder(id: Long): LiveData<Reminder> {
        return reminderDao.getReminder(id).asLiveData()
    }

    fun convertEpochToDate(epoch: Long): String {
        return LocalDateTime.ofInstant(
            Instant.ofEpochSecond(epoch),
            ZoneId.systemDefault()).toLocalDate().toString()
    }

    fun convertEpochToTime(epoch: Long): String {
        return LocalDateTime.ofInstant(
            Instant.ofEpochSecond(epoch),
            ZoneId.systemDefault()).toLocalTime().toString()
    }

    fun convertRepeatInterval(repeatInterval: Long): String {
        val daysInYear = 365
        var period = Duration.ofSeconds(repeatInterval)

        val totalDays = period.toDays()
        val years = totalDays.div(daysInYear)
        val days = totalDays % daysInYear

        period = period.minusDays(totalDays)
        val hours = period.toHours()

        period = period.minusHours(hours)
        val minutes = period.toMinutes()

        return formatRepeatInterval(years, days, hours, minutes)
    }

    private fun formatRepeatInterval(years: Long, days: Long, hours: Long, minutes: Long): String {
        val repeatInterval = StringJoiner(", ")

        if (years > 0) repeatInterval.add("$years years")
        if (days > 0) repeatInterval.add("$days days")
        if (hours > 0) repeatInterval.add("$hours hours")
        if (minutes > 0) repeatInterval.add("$minutes minutes")

        return repeatInterval.toString()
    }
}

class ReminderDetailsViewModelFactory(
    private val reminderDao: ReminderDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReminderDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReminderDetailsViewModel(reminderDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}