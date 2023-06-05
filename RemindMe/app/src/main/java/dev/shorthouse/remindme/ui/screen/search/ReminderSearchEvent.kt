package dev.shorthouse.remindme.ui.screen.search

sealed interface ReminderSearchEvent {
    data class UpdateSearchQuery(val query: String) : ReminderSearchEvent
    object RemoveSnackbarMessage : ReminderSearchEvent
}
