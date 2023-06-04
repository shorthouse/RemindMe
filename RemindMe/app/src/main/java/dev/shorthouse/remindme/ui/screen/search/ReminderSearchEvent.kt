package dev.shorthouse.remindme.ui.screen.search

sealed interface ReminderSearchEvent {
    data class Search(val query: String) : ReminderSearchEvent
}
