package dev.shorthouse.remindme.compose.state

import androidx.compose.runtime.*
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.model.RepeatInterval
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

fun ReminderState(): ReminderState = ReminderStateImpl()
fun ReminderState(reminder: Reminder): ReminderState = ReminderStateImpl(reminder)
fun ReminderState(
    name: String,
    date: String,
    time: LocalTime,
    isNotificationSent: Boolean,
    isRepeatReminder: Boolean,
    repeatAmount: String,
    repeatUnit: String,
    notes: String?
): ReminderState = ReminderStateImpl(
    name = name,
    date = date,
    time = time,
    isNotificationSent = isNotificationSent,
    isRepeatReminder = isRepeatReminder,
    repeatAmount = repeatAmount,
    repeatUnit = repeatUnit,
    notes = notes
)

@Stable
interface ReminderState {
    var name: String
    var date: String
    var time: LocalTime
    var isNotificationSent: Boolean
    var isRepeatReminder: Boolean
    var repeatAmount: String
    var repeatUnit: String
    var notes: String?

    fun toReminder(): Reminder
}

private class ReminderStateImpl(
    name: String = "",
    date: String = getDateToday(),
    time: LocalTime = getTimeNextHour(),
    isNotificationSent: Boolean = false,
    isRepeatReminder: Boolean = false,
    repeatAmount: String = "1",
    repeatUnit: String = "Day",
    notes: String? = ""
) : ReminderState {
    constructor(reminder: Reminder) : this(
        name = reminder.name,
        date = getStateDate(reminder.startDateTime),
        time = getStateTime(reminder.startDateTime),
        isNotificationSent = reminder.isNotificationSent,
        isRepeatReminder = reminder.repeatInterval != null,
        repeatAmount = reminder.repeatInterval?.amount?.toString() ?: "1",
        repeatUnit = getStateRepeatUnit(reminder.repeatInterval),
        notes = reminder.notes
    )

    companion object {
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy")
        val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm")

        private fun getDateToday(): String {
            return ZonedDateTime.now()
                .toLocalDate()
                .format(dateFormatter)
                .toString()
        }

        private fun getTimeNextHour(): LocalTime {
            return ZonedDateTime.now()
                .truncatedTo(ChronoUnit.HOURS)
                .plusHours(1)
                .toLocalTime()
        }

        private fun getStateDate(zonedDateTime: ZonedDateTime): String {
            return zonedDateTime
                .toLocalDate()
                .format(dateFormatter)
                .toString()
        }

        private fun getStateTime(zonedDateTime: ZonedDateTime): LocalTime {
            return zonedDateTime
                .toLocalTime()
        }

        private fun getStateRepeatUnit(repeatInterval: RepeatInterval?): String {
            if (repeatInterval == null) return "Day"

            return when {
                repeatInterval.unit == ChronoUnit.DAYS && repeatInterval.amount == 1L -> "Day"
                repeatInterval.unit == ChronoUnit.DAYS && repeatInterval.amount != 1L -> "Days"
                repeatInterval.unit == ChronoUnit.WEEKS && repeatInterval.amount == 1L -> "Week"
                else -> "Weeks"
            }
        }

        private fun getReminderStartDateTime(date: String, time: LocalTime): ZonedDateTime {
            return LocalDateTime
                .parse("$date $time", dateTimeFormatter)
                .atZone(ZoneId.systemDefault())
        }

        private fun getReminderRepeatInterval(repeatAmount: String, repeatUnit: String): RepeatInterval {
            return RepeatInterval(
                amount = repeatAmount.toLongOrNull() ?: 1,
                unit = when {
                    repeatUnit.contains("day") -> ChronoUnit.DAYS
                    else -> ChronoUnit.WEEKS
                }
            )
        }
    }

    override fun toReminder(): Reminder {
        return Reminder(
            name = _name,
            startDateTime = getReminderStartDateTime(_date, _time),
            isNotificationSent = _isNotificationSent,
            repeatInterval = if (isRepeatReminder) getReminderRepeatInterval(_repeatAmount, _repeatUnit) else null,
            notes = _notes?.trim()?.ifBlank { null },
            isComplete = false
        )
    }

    private var _name by mutableStateOf(name, structuralEqualityPolicy())
    override var name: String
        get() = _name
        set(value) {
            _name = value
        }

    private var _date by mutableStateOf(date, structuralEqualityPolicy())
    override var date: String
        get() = _date
        set(value) {
            _date = value
        }

    private var _time by mutableStateOf(time, structuralEqualityPolicy())
    override var time: LocalTime
        get() = _time
        set(value) {
            _time = value
        }

    private var _isNotificationSent by mutableStateOf(isNotificationSent, structuralEqualityPolicy())
    override var isNotificationSent: Boolean
        get() = _isNotificationSent
        set(value) {
            _isNotificationSent = value
        }

    private var _isRepeatReminder by mutableStateOf(isRepeatReminder, structuralEqualityPolicy())
    override var isRepeatReminder: Boolean
        get() = _isRepeatReminder
        set(value) {
            _isRepeatReminder = value
        }

    private var _repeatAmount by mutableStateOf(repeatAmount, structuralEqualityPolicy())
    override var repeatAmount: String
        get() = _repeatAmount
        set(value) {
            _repeatAmount = value
        }

    private var _repeatUnit by mutableStateOf(repeatUnit, structuralEqualityPolicy())
    override var repeatUnit: String
        get() = _repeatUnit
        set(value) {
            _repeatUnit = value
        }

    private var _notes by mutableStateOf(notes, structuralEqualityPolicy())
    override var notes: String?
        get() = _notes
        set(value) {
            _notes = value
        }
}
