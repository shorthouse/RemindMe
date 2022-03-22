package dev.shorthouse.remindme.viewmodel

import androidx.lifecycle.*
import androidx.work.*
import dev.shorthouse.remindme.BaseApplication
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.data.ReminderDao
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.utilities.KEY_REMINDER_NAME
import dev.shorthouse.remindme.utilities.NOTIFICATION_UNIQUE_WORK_NAME_PREFIX
import dev.shorthouse.remindme.workers.ReminderNotificationWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class AddReminderViewModel(
    application: BaseApplication,
    private val reminderDao: ReminderDao
) : ViewModel() {

    private val workManager = WorkManager.getInstance(application)
    private val dateFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy")
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm")

    fun addReminder(
        name: String,
        startDateTime: ZonedDateTime,
        reminderInterval: Long?,
        notes: String?,
        isArchived: Boolean,
        isNotificationSent: Boolean
    ) {
        val reminder = Reminder(
            name = name,
            startDateTime = startDateTime,
            repeatInterval = reminderInterval,
            notes = notes,
            isArchived = isArchived,
            isNotificationSent = isNotificationSent
        )

        viewModelScope.launch(Dispatchers.IO) {
            val id = reminderDao.insert(reminder)
            if (isNotificationSent) scheduleNotification(id, reminder)
        }
    }

    fun updateReminder(
        id: Long,
        name: String,
        startDateTime: ZonedDateTime,
        reminderInterval: Long?,
        notes: String?,
        isArchived: Boolean,
        isNotificationSent: Boolean
    ) {
        val reminder = Reminder(
            id = id,
            name = name,
            startDateTime = startDateTime,
            repeatInterval = reminderInterval,
            notes = notes,
            isArchived = isArchived,
            isNotificationSent = isNotificationSent
        )

        viewModelScope.launch(Dispatchers.IO) {
            reminderDao.update(reminder)
            if (isNotificationSent) scheduleNotification(id, reminder)
        }
    }

    fun getReminder(id: Long): LiveData<Reminder> {
        return reminderDao.getReminder(id).asLiveData()
    }

    private fun scheduleNotification(reminderId: Long, reminder: Reminder) {
        if (reminder.repeatInterval == null) {
            workManager.enqueueUniqueWork(
                NOTIFICATION_UNIQUE_WORK_NAME_PREFIX + reminderId,
                ExistingWorkPolicy.REPLACE,
                getOneTimeNotificationWorker(reminder)
            )
        } else {
            workManager.enqueueUniquePeriodicWork(
                NOTIFICATION_UNIQUE_WORK_NAME_PREFIX + reminderId,
                ExistingPeriodicWorkPolicy.REPLACE,
                getRepeatNotificationWorker(reminder)
            )
        }
    }

    private fun getRepeatNotificationWorker(reminder: Reminder): PeriodicWorkRequest {
        return PeriodicWorkRequestBuilder<ReminderNotificationWorker>(
            Duration.ofSeconds(reminder.repeatInterval!!)
        )
            .setInitialDelay(getDurationUntilReminder(reminder.startDateTime))
            .setInputData(createInputData(reminder))
            .build()
    }

    private fun getOneTimeNotificationWorker(reminder: Reminder): OneTimeWorkRequest {
        return OneTimeWorkRequestBuilder<ReminderNotificationWorker>()
            .setInitialDelay(getDurationUntilReminder(reminder.startDateTime))
            .setInputData(createInputData(reminder))
            .build()
    }

    private fun createInputData(reminder: Reminder): Data {
        return Data.Builder()
            .putString(KEY_REMINDER_NAME, reminder.name)
            .build()
    }

    private fun getDurationUntilReminder(startDateTime: ZonedDateTime): Duration {
        return Duration.ofSeconds(
            startDateTime
                .minusSeconds(Instant.now().epochSecond)
                .toEpochSecond()
        )
    }

    fun convertDateTimeStringToDateTime(dateText: String, timeText: String): ZonedDateTime {
        return LocalDateTime.parse(
            "$dateText $timeText",
            dateTimeFormatter
        )
            .atZone(ZoneId.systemDefault())
    }

    fun getStartDate(reminder: Reminder?): String {
        return when (reminder) {
            null -> ZonedDateTime.now().toLocalDate().format(dateFormatter).toString()
            else -> reminder.startDateTime.toLocalDate().format(dateFormatter).toString()
        }
    }

    fun convertTimestampToDateString(dateTimestamp: Long): String {
        return Instant.ofEpochMilli(dateTimestamp)
            .atZone(ZoneId.systemDefault())
            .format(dateFormatter)
    }

    fun getStartTime(reminder: Reminder?): String {
        return when (reminder) {
            null -> getCurrentTimeNextHour()
            else -> reminder.startDateTime.toLocalTime().toString()
        }
    }

    private fun getCurrentTimeNextHour(): String {
        return ZonedDateTime.now()
            .truncatedTo(ChronoUnit.HOURS).plusHours(1)
            .toLocalTime().toString()
    }

    fun getIsRepeatChecked(reminder: Reminder?): Boolean {
        return if (reminder == null) false else reminder.repeatInterval != null
    }

    fun getCheckedRadioButton(reminder: Reminder?): Int {
        if (reminder == null) return R.id.radio_repeat_daily

        return when (reminder.repeatInterval) {
            Duration.ofDays(1).seconds -> R.id.radio_repeat_daily
            Duration.ofDays(7).seconds -> R.id.radio_repeat_weekly
            Duration.ofDays(30).seconds -> R.id.radio_repeat_monthly
            else -> R.id.radio_repeat_yearly
        }

        val test = LocalDateTime.of(2020, 03, 20, 13, 0)
        test.plusMonths(1)
        test.plusYears()
    }

    // TODO months should be just +1 to the month
    // TODO year should be just +1 to the year
    fun getReminderIntervalSeconds(timeUnit: ChronoUnit): Long {
        return when (timeUnit) {
            ChronoUnit.DAYS -> Duration.ofDays(1).seconds
            ChronoUnit.WEEKS -> Duration.ofDays(7).seconds
            ChronoUnit.MONTHS -> Duration.ofDays(30).seconds
            else -> Duration.ofDays(365).seconds
        }
    }

    fun isDetailValid(name: String, startDateTime: ZonedDateTime): Boolean {
        return when {
            name.isBlank() -> false
            startDateTime.isBefore(ZonedDateTime.now()) -> false
            else -> true
        }
    }

    fun getDetailError(name: String): Int {
        return when {
            name.isBlank() -> R.string.error_name_empty
            else -> R.string.error_time_past
        }
    }
}

class AddEditReminderViewModelFactory(
    private val application: BaseApplication,
    private val reminderDao: ReminderDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddReminderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddReminderViewModel(application, reminderDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}