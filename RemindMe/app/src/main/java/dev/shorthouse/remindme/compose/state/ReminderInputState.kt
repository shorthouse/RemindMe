package dev.shorthouse.remindme.compose.state

import androidx.compose.runtime.*
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.model.InputRepeatInterval

@Stable
interface ReminderInputState {
    var reminderName: String
    var isNotificationSent: Boolean
    var isRepeatReminder: Boolean
    var repeatInterval: InputRepeatInterval
}

private class ReminderInputStateImpl(
    reminderName: String = "",
    isNotificationSent: Boolean = false,
    isRepeatReminder: Boolean = false,
    repeatInterval: InputRepeatInterval = InputRepeatInterval(
        R.plurals.interval_days,
        1
    )
) : ReminderInputState {
    private var _reminderName by mutableStateOf(reminderName, structuralEqualityPolicy())

    override var reminderName: String
        get() = _reminderName
        set(value) {
            require(value.length <= 200) { "Reminder name length must be <= 200" }
            _reminderName = value
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

    private var _repeatInterval by mutableStateOf(repeatInterval, structuralEqualityPolicy())

    // TODO dont think custom set needed if just setting value
    override var repeatInterval: InputRepeatInterval
        get() = _repeatInterval
        set(value) {
            _repeatInterval = value
        }
}

fun ReminderInputState(): ReminderInputState = ReminderInputStateImpl()
