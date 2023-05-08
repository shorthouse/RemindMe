package dev.shorthouse.remindme.domain.userpreferences

import dev.shorthouse.remindme.data.protodatastore.ReminderSort
import dev.shorthouse.remindme.data.protodatastore.UserPreferencesRepository
import dev.shorthouse.remindme.di.IoDispatcher
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class UpdateReminderSortOrderUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(sortOrder: ReminderSort) {
        updateReminderSortOrderUseCase(sortOrder = sortOrder)
    }

    private suspend fun updateReminderSortOrderUseCase(sortOrder: ReminderSort) {
        withContext(ioDispatcher) {
            userPreferencesRepository.updateReminderSortOrder(reminderSortOrder = sortOrder)
        }
    }
}
