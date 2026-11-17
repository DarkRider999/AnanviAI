package com.aiuniverse.app.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiuniverse.app.domain.model.AppSettings
import com.aiuniverse.app.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val settings: StateFlow<AppSettings> = settingsRepository.getSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

    fun updateTheme(themeMode: String) {
        viewModelScope.launch {
            val current = settings.value
            settingsRepository.updateSettings(current.copy(themeMode = themeMode))
        }
    }

    fun toggleStreaming(enabled: Boolean) {
        viewModelScope.launch {
            val current = settings.value
            settingsRepository.updateSettings(current.copy(streamResponses = enabled))
        }
    }

    fun toggleHaptics(enabled: Boolean) {
        viewModelScope.launch {
            val current = settings.value
            settingsRepository.updateSettings(current.copy(hapticFeedback = enabled))
        }
    }

    fun updateTemperature(temp: Float) {
        viewModelScope.launch {
            val current = settings.value
            settingsRepository.updateSettings(current.copy(temperature = temp))
        }
    }
}
