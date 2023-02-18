package dev.shorthouse.remindme.domain.reminder

import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class DeleteCompletedRemindersUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {
    private val coroutineScope = CoroutineScope(ioDispatcher)

    operator fun invoke() {
        deleteCompletedReminders()
    }

    private fun deleteCompletedReminders() {
        coroutineScope.launch {
            reminderRepository.deleteCompletedReminders()
        }
    }
}
