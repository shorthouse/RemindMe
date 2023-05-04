package dev.shorthouse.remindme.ui.search

sealed interface ReminderSearchEvent {
    data class Search(val query: String) : ReminderSearchEvent
}
