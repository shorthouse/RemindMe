package dev.shorthouse.remindme.domain.reminder

import dev.shorthouse.remindme.data.source.local.ReminderRepository
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.model.Reminder
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class GetReminderUseCase @Inject constructor(
    private val reminderRepository: ReminderRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(reminderId: Long): Reminder {
        return getReminder(reminderId = reminderId)
    }

    private suspend fun getReminder(reminderId: Long): Reminder {
        return withContext(ioDispatcher) {
            reminderRepository.getReminderOneShot(reminderId)
        }
    }
}
