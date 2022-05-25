package dev.shorthouse.remindme.utilities

import dev.shorthouse.remindme.model.Reminder

fun Reminder.isRepeatReminder(): Boolean {
    return this.repeatInterval != null
}