package com.aiuniverse.app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiuniverse.app.domain.model.AIProvider
import com.aiuniverse.app.domain.model.ChatSession
import com.aiuniverse.app.domain.model.UserProfile
import com.aiuniverse.app.domain.repository.AIProviderRepository
import com.aiuniverse.app.domain.repository.ChatRepository
import com.aiuniverse.app.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val providerRepository: AIProviderRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val recentSessions: StateFlow<List<ChatSession>> = chatRepository.getSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val providers: StateFlow<List<AIProvider>> = providerRepository.getProviders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfile> = settingsRepository.getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

    fun createNewChat(onCreated: (String) -> Unit) {
        viewModelScope.launch {
            val session = chatRepository.createSession(
                title = "New Chat",
                providerId = "gemini",
                modelId = "gemini-3.8-flash"
            )
            onCreated(session.id)
        }
    }
}
