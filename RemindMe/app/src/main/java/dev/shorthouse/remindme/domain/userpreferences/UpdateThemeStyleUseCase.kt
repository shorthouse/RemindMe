package dev.shorthouse.remindme.domain.userpreferences

import dev.shorthouse.remindme.data.protodatastore.ThemeStyle
import dev.shorthouse.remindme.data.protodatastore.UserPreferencesRepository
import dev.shorthouse.remindme.di.IoDispatcher
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class UpdateThemeStyleUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(themeStyle: ThemeStyle) {
        updateThemeStyle(themeStyle = themeStyle)
    }

    private suspend fun updateThemeStyle(themeStyle: ThemeStyle) {
        withContext(ioDispatcher) {
            userPreferencesRepository.updateThemeStyle(themeStyle = themeStyle)
        }
    }
}
