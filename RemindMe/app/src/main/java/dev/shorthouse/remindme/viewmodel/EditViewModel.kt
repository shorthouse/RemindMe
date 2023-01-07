package dev.shorthouse.remindme.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dev.shorthouse.remindme.data.ReminderRepository
import javax.inject.Inject

class EditViewModel @Inject constructor(
    repository: ReminderRepository,
    state: SavedStateHandle
) : ViewModel() {
    private val reminderId = state.get<Long>("id") ?: 1L
    val reminder = repository.getReminder(reminderId).asLiveData()
}
