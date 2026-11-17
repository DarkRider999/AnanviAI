package com.aiuniverse.app.domain.usecase

import com.aiuniverse.app.domain.model.ChatSession
import com.aiuniverse.app.domain.repository.ChatRepository
import javax.inject.Inject

class CreateChatSessionUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(
        title: String = "New Conversation",
        providerId: String = "huggingface",
        modelId: String = "meta-llama/Llama-3.1-8B-Instruct",
        systemPrompt: String? = null
    ): ChatSession {
        return repository.createSession(title, providerId, modelId, systemPrompt)
    }
}
