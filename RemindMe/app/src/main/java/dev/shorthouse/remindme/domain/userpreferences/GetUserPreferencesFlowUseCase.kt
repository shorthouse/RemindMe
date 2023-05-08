package dev.shorthouse.remindme.domain.userpreferences

import dev.shorthouse.remindme.data.protodatastore.UserPreferences
import dev.shorthouse.remindme.data.protodatastore.UserPreferencesRepository
import dev.shorthouse.remindme.di.IoDispatcher
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class GetUserPreferencesFlowUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    operator fun invoke(): Flow<UserPreferences> {
        return getUserPreferencesFlow()
    }

    private fun getUserPreferencesFlow(): Flow<UserPreferences> {
        return userPreferencesRepository.userPreferencesFlow
            .flowOn(ioDispatcher)
    }
}
