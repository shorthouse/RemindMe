package dev.shorthouse.remindme.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.data.protodatastore.Theme
import dev.shorthouse.remindme.data.protodatastore.UserPreferencesRepository
import dev.shorthouse.remindme.di.IoDispatcher
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())

    val uiState = _uiState.asStateFlow()

    init {
        initialiseUiState()
    }

    private fun initialiseUiState() {
        _uiState.update { it.copy(isLoading = true) }

        userPreferencesRepository.userPreferencesFlow
            .flowOn(ioDispatcher)
            .onEach { userPreferences ->
                _uiState.update {
                    it.copy(
                        theme = userPreferences.theme,
                        isNotificationOnByDefault = userPreferences.isNotificationOnByDefault,
                        isLoading = false
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun handleEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.SetTheme -> handleSetTheme(event.theme)
            is SettingsEvent.SetNotification ->
                handleSetNotificationDefault(event.isNotificationOnByDefault)
        }
    }

    private fun handleSetTheme(theme: Theme) {
        viewModelScope.launch(ioDispatcher) {
            userPreferencesRepository.updateTheme(theme)
        }
    }

    private fun handleSetNotificationDefault(isNotificationOnByDefault: Boolean) {
        viewModelScope.launch(ioDispatcher) {
            userPreferencesRepository.updateIsNotificationOnByDefault(isNotificationOnByDefault)
        }
    }
}
