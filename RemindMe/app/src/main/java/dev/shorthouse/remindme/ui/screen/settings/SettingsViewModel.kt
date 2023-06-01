package dev.shorthouse.remindme.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.data.protodatastore.ThemeStyle
import dev.shorthouse.remindme.domain.userpreferences.GetUserPreferencesFlowUseCase
import dev.shorthouse.remindme.domain.userpreferences.UpdateNotificationDefaultUseCase
import dev.shorthouse.remindme.domain.userpreferences.UpdateThemeStyleUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getUserPreferencesFlowUseCase: GetUserPreferencesFlowUseCase,
    private val updateThemeStyleUseCase: UpdateThemeStyleUseCase,
    private val updateNotificationDefaultUseCase: UpdateNotificationDefaultUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())

    val uiState = _uiState.asStateFlow()

    init {
        initialiseUiState()
    }

    private fun initialiseUiState() {
        _uiState.update { it.copy(isLoading = true) }

        getUserPreferencesFlowUseCase()
            .onEach { userPreferences ->
                _uiState.update {
                    it.copy(
                        themeStyle = userPreferences.themeStyle,
                        isNotificationDefaultOn = userPreferences.isNotificationDefaultOn,
                        isLoading = false
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun handleEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.Theme -> handleAppTheme(event.theme)
            is SettingsEvent.NotificationDefault -> handleNotificationDefault(event.isDefaultOn)
        }
    }

    private fun handleAppTheme(themeStyle: ThemeStyle) {
        viewModelScope.launch {
            updateThemeStyleUseCase(themeStyle = themeStyle)
        }
    }

    private fun handleNotificationDefault(isDefaultOn: Boolean) {
        viewModelScope.launch {
            updateNotificationDefaultUseCase(isDefaultOn = isDefaultOn)
        }
    }
}
