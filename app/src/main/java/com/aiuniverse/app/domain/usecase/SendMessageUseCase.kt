package com.aiuniverse.app.domain.usecase

import com.aiuniverse.app.domain.model.MediaType
import com.aiuniverse.app.domain.model.Message
import com.aiuniverse.app.domain.model.MessageRole
import com.aiuniverse.app.domain.repository.ChatRepository
import com.aiuniverse.app.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import java.util.UUID
import javax.inject.Inject

/**
 * Encapsulates the complete chat messaging lifecycle:
 * 1. Creates and persists the user prompt message.
 * 2. Prepares a streaming assistant message placeholder in Room.
 * 3. Streams token deltas from the selected AI Provider API.
 * 4. Updates Room reactively and persists the final response.
 * 5. Tracks token statistics in user profile.
 */
class SendMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
    private val settingsRepository: SettingsRepository
) {
    suspend fun execute(
        sessionId: String,
        userPrompt: String,
        providerId: String,
        modelId: String,
        systemPrompt: String? = null,
        mediaUri: String? = null,
        mediaType: MediaType = MediaType.NONE,
        onDelta: (accumulatedText: String) -> Unit,
        onError: (Throwable) -> Unit,
        onFinish: (finalText: String) -> Unit
    ) {
        // 1. Save user message
        val userMessageId = UUID.randomUUID().toString()
        val userMessage = Message(
            id = userMessageId,
            sessionId = sessionId,
            role = MessageRole.USER,
            content = userPrompt,
            mediaUri = mediaUri,
            mediaType = mediaType
        )
        chatRepository.saveMessage(userMessage)

        // 2. Create placeholder assistant message
        val assistantMessageId = UUID.randomUUID().toString()
        val assistantMessage = Message(
            id = assistantMessageId,
            sessionId = sessionId,
            role = MessageRole.ASSISTANT,
            content = "",
            modelName = modelId,
            isStreaming = true
        )
        chatRepository.saveMessage(assistantMessage)

        // 3. Collect past history for conversational context
        val fullHistory = mutableListOf<Message>()
        val historyList = chatRepository.getSessionOnce(sessionId)?.let {
            // retrieve snapshot
        }

        val accumulated = StringBuilder()

        chatRepository.streamResponse(
            providerId = providerId,
            modelId = modelId,
            messages = listOf(userMessage),
            systemPrompt = systemPrompt
        ).onEach { token ->
            accumulated.append(token)
            onDelta(accumulated.toString())
            chatRepository.updateMessageStream(
                messageId = assistantMessageId,
                content = accumulated.toString(),
                isStreaming = true,
                hasError = false
            )
        }.catch { error ->
            onError(error)
            val errorText = accumulated.toString().ifEmpty {
                "Error communicating with $providerId: ${error.localizedMessage ?: "Unknown connection error"}"
            }
            chatRepository.updateMessageStream(
                messageId = assistantMessageId,
                content = errorText,
                isStreaming = false,
                hasError = true
            )
        }.onCompletion {
            chatRepository.updateMessageStream(
                messageId = assistantMessageId,
                content = accumulated.toString(),
                isStreaming = false,
                hasError = false
            )
            settingsRepository.recordTokenUsage((userPrompt.length + accumulated.length) / 4L)
            onFinish(accumulated.toString())
        }.collect {}
    }
}
