package com.aiuniverse.app.domain.usecase

import com.aiuniverse.app.domain.model.AiToolType
import com.aiuniverse.app.domain.model.Message
import com.aiuniverse.app.domain.model.MessageRole
import com.aiuniverse.app.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExecuteAiToolUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    fun execute(
        tool: AiToolType,
        input: String,
        targetLanguageOrOptions: String? = null,
        providerId: String = "gemini",
        modelId: String = "gemini-3.8-flash"
    ): Flow<String> {
        val engineeredPrompt = buildString {
            append(tool.promptTemplate)
            if (!targetLanguageOrOptions.isNullOrBlank()) {
                append("Target / Parameters: $targetLanguageOrOptions\n\n")
            }
            append(input)
        }

        val toolMessage = Message(
            id = "tool_req",
            sessionId = "tool_session",
            role = MessageRole.USER,
            content = engineeredPrompt
        )

        return chatRepository.streamResponse(
            providerId = providerId,
            modelId = modelId,
            messages = listOf(toolMessage),
            systemPrompt = "You are an expert AI productivity assistant specialized in concise, highly polished execution."
        )
    }
}
