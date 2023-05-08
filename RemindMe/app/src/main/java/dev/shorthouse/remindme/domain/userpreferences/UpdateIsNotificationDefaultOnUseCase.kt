package dev.shorthouse.remindme.domain.userpreferences

import dev.shorthouse.remindme.data.protodatastore.UserPreferencesRepository
import dev.shorthouse.remindme.di.IoDispatcher
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class UpdateIsNotificationDefaultOnUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(isNotificationDefaultOn: Boolean) {
        updateIsNotificationDefaultOnUseCase(isNotificationDefaultOn = isNotificationDefaultOn)
    }

    private suspend fun updateIsNotificationDefaultOnUseCase(isNotificationDefaultOn: Boolean) {
        withContext(ioDispatcher) {
            userPreferencesRepository.updateIsNotificationDefaultOn(
                isNotificationDefaultOn = isNotificationDefaultOn
            )
        }
    }
}
