package com.aiuniverse.app.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiuniverse.app.domain.model.UserProfile
import com.aiuniverse.app.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val profile: StateFlow<UserProfile> = settingsRepository.getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

    private val _systemPromptInput = MutableStateFlow("")
    val systemPromptInput: StateFlow<String> = _systemPromptInput.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.getUserProfile().collect { p ->
                _systemPromptInput.value = p.customSystemPrompt
            }
        }
    }

    fun onSystemPromptChanged(newPrompt: String) {
        _systemPromptInput.value = newPrompt
    }

    fun saveCustomSystemPrompt() {
        viewModelScope.launch {
            val current = profile.value
            settingsRepository.updateUserProfile(
                current.copy(customSystemPrompt = _systemPromptInput.value.trim())
            )
        }
    }
}
