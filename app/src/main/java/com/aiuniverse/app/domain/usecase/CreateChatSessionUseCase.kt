package com.aiuniverse.app.domain.usecase

import com.aiuniverse.app.domain.model.ChatSession
import com.aiuniverse.app.domain.repository.ChatRepository
import javax.inject.Inject

class CreateChatSessionUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(
        title: String = "New Conversation",
        providerId: String = "gemini",
        modelId: String = "gemini-3.8-flash",
        systemPrompt: String? = null
    ): ChatSession {
        return repository.createSession(title, providerId, modelId, systemPrompt)
    }
}
