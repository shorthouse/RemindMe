package dev.shorthouse.habitbuilder.viewmodels

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
import java.time.format.DateTimeFormatter
import java.util.*

class ReminderDetailsViewModel(private val reminderDao: ReminderDao
) : ViewModel() {

    companion object {
        private val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
        private const val DAYS_IN_YEAR = 365L
    }

    fun getReminder(id: Long): LiveData<Reminder> {
        return reminderDao.getReminder(id).asLiveData()
    }

    fun convertEpochToDate(epoch: Long): String {
        return Instant.ofEpochSecond(epoch)
            .atZone(ZoneId.systemDefault())
            .format(dateFormatter)
    }

    fun convertEpochToTime(epoch: Long): String {
        return getLocalDateTime(epoch).toLocalTime().toString()
    }

    private fun getLocalDateTime(epoch: Long): LocalDateTime {
        return LocalDateTime.ofInstant(
            Instant.ofEpochSecond(epoch),
            ZoneId.systemDefault()
        )
    }

    fun convertRepeatInterval(repeatInterval: Long?): String {
        if (repeatInterval == null) {
            return ""
        }

        val period = Duration.ofSeconds(repeatInterval)
        val totalDays = period.toDays()

        val years = totalDays.div(DAYS_IN_YEAR)
        val days = totalDays.mod(DAYS_IN_YEAR)
        val hours = period.minusDays(totalDays).toHours()

        return formatRepeatInterval(years, days, hours)
    }

    private fun formatRepeatInterval(years: Long, days: Long, hours: Long): String {
        val repeatInterval = StringJoiner(", ")

        if (years > 0) repeatInterval.add("$years years")
        if (days > 0) repeatInterval.add("$days days")
        if (hours > 0) repeatInterval.add("$hours hours")

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