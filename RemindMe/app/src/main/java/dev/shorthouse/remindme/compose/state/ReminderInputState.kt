package dev.shorthouse.remindme.compose.state

import androidx.compose.runtime.*

@Stable
interface ReminderInputState {
    var reminderName: String
    var isNotificationSent: Boolean
    var isRepeatReminder: Boolean
    var repeatAmount: String
    var repeatUnit: String
}

private class ReminderInputStateImpl(
    reminderName: String = "",
    isNotificationSent: Boolean = false,
    isRepeatReminder: Boolean = false,
    repeatAmount: String = "1",
    repeatUnit: String = "Day",
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
}

fun ReminderInputState(): ReminderInputState = ReminderInputStateImpl()
