package dev.shorthouse.remindme.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.data.protodatastore.ThemeStyle
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
                        themeStyle = userPreferences.themeStyle,
                        isNotificationOnByDefault = userPreferences.isNotificationDefaultOn,
                        isLoading = false
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun handleEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.Theme -> handleAppTheme(event.theme)
            is SettingsEvent.NotificationDefault ->
                handleNotificationDefault(event.isNotificationDefaultOn)
        }
    }

    private fun handleAppTheme(theme: ThemeStyle) {
        viewModelScope.launch(ioDispatcher) {
            userPreferencesRepository.updateThemeStyle(theme)
        }
    }

    private fun handleNotificationDefault(isNotificationDefaultOn: Boolean) {
        viewModelScope.launch(ioDispatcher) {
            userPreferencesRepository.updateIsNotificationDefaultOn(isNotificationDefaultOn)
        }
    }
}
