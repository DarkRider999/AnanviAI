package com.aiuniverse.app.presentation.chat

import com.aiuniverse.app.domain.model.AIModel
import com.aiuniverse.app.domain.model.ChatSession
import com.aiuniverse.app.domain.model.MediaType
import com.aiuniverse.app.domain.model.Message

data class ChatUiState(
    val session: ChatSession? = null,
    val messages: List<Message> = emptyList(),
    val inputText: String = "",
    val isStreaming: Boolean = false,
    val selectedProviderId: String = "gemini",
    val selectedModelId: String = "gemini-3.8-flash",
    val availableModels: List<AIModel> = emptyList(),
    val attachedUri: String? = null,
    val attachedMediaType: MediaType = MediaType.NONE,
    val isVoiceModalOpen: Boolean = false,
    val errorMessage: String? = null
)
