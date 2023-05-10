package dev.shorthouse.remindme.domain.userpreferences

import dev.shorthouse.remindme.data.protodatastore.ReminderFilter
import dev.shorthouse.remindme.data.protodatastore.UserPreferencesRepository
import dev.shorthouse.remindme.di.IoDispatcher
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class UpdateReminderFilterUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(filter: ReminderFilter) {
        return updateReminderFilter(filter = filter)
    }

    private suspend fun updateReminderFilter(filter: ReminderFilter) {
        withContext(ioDispatcher) {
            userPreferencesRepository.updateReminderFilter(reminderFilter = filter)
        }
    }
}
