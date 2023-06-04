package dev.shorthouse.remindme.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.domain.userpreferences.GetUserPreferencesFlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val getUserPreferencesFlowUseCase: GetUserPreferencesFlowUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainActivityUiState())

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
                        isLoading = false
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}
