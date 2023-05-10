package dev.shorthouse.remindme.domain.userpreferences

import dev.shorthouse.remindme.data.protodatastore.UserPreferencesRepository
import dev.shorthouse.remindme.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpdateNotificationDefaultUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(isDefaultOn: Boolean) {
        updateNotificationDefaultUseCase(isDefaultOn = isDefaultOn)
    }

    private suspend fun updateNotificationDefaultUseCase(isDefaultOn: Boolean) {
        withContext(ioDispatcher) {
            userPreferencesRepository.updateNotificationDefault(
                isDefaultOn = isDefaultOn
            )
        }
    }
}
