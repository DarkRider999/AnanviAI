package com.aiuniverse.app.data.repository

import com.aiuniverse.app.data.local.dao.ChatSessionDao
import com.aiuniverse.app.data.local.dao.MessageDao
import com.aiuniverse.app.data.local.entity.ChatSessionEntity
import com.aiuniverse.app.data.local.entity.MessageEntity
import com.aiuniverse.app.data.remote.api.GroqApi
import com.aiuniverse.app.data.remote.api.HuggingFaceApi
import com.aiuniverse.app.data.remote.api.OllamaApi
import com.aiuniverse.app.data.remote.api.OpenAIApi
import com.aiuniverse.app.data.remote.api.OpenRouterApi
import com.aiuniverse.app.data.remote.api.TogetherApi
import com.aiuniverse.app.data.remote.dto.ChatCompletionRequest
import com.aiuniverse.app.data.remote.dto.ChatMessageDto
import com.aiuniverse.app.data.remote.dto.HuggingFaceInferenceRequest
import com.aiuniverse.app.data.remote.dto.HuggingFaceParameters
import com.aiuniverse.app.data.remote.sse.SseStreamParser
import com.aiuniverse.app.domain.model.ChatSession
import com.aiuniverse.app.domain.model.Message
import com.aiuniverse.app.domain.model.MessageRole
import com.aiuniverse.app.domain.repository.ChatRepository
import com.aiuniverse.app.security.SecureKeyStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val sessionDao: ChatSessionDao,
    private val messageDao: MessageDao,
    private val huggingFaceApi: HuggingFaceApi,
    private val openRouterApi: OpenRouterApi,
    private val groqApi: GroqApi,
    private val openAiApi: OpenAIApi,
    private val ollamaApi: OllamaApi,
    private val togetherApi: TogetherApi,
    private val keyStorage: SecureKeyStorage,
    private val sseParser: SseStreamParser
) : ChatRepository {

    override fun getSessions(): Flow<List<ChatSession>> {
        return sessionDao.getAllSessions().map { list -> list.map { it.toDomain() } }
    }

    override fun getSession(sessionId: String): Flow<ChatSession?> {
        return sessionDao.getSessionFlow(sessionId).map { it?.toDomain() }
    }

    override suspend fun getSessionOnce(sessionId: String): ChatSession? {
        return sessionDao.getSessionById(sessionId)?.toDomain()
    }

    override fun getMessages(sessionId: String): Flow<List<Message>> {
        return messageDao.getMessagesForSession(sessionId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun createSession(
        title: String,
        providerId: String,
        modelId: String,
        systemPrompt: String?
    ): ChatSession {
        val newSession = ChatSession(
            id = UUID.randomUUID().toString(),
            title = title,
            providerId = providerId,
            modelId = modelId,
            systemPrompt = systemPrompt
        )
        sessionDao.insertSession(ChatSessionEntity.fromDomain(newSession))
        return newSession
    }

    override suspend fun updateSessionTitle(sessionId: String, title: String) {
        val session = sessionDao.getSessionById(sessionId) ?: return
        sessionDao.updateSession(session.copy(title = title, updatedAt = System.currentTimeMillis()))
    }

    override suspend fun pinSession(sessionId: String, isPinned: Boolean) {
        sessionDao.updatePinStatus(sessionId, isPinned)
    }

    override suspend fun deleteSession(sessionId: String) {
        messageDao.deleteMessagesForSession(sessionId)
        sessionDao.deleteSessionById(sessionId)
    }

    override suspend fun saveMessage(message: Message) {
        messageDao.insertMessage(MessageEntity.fromDomain(message))
        sessionDao.touchSession(message.sessionId)
    }

    override suspend fun updateMessageStream(
        messageId: String,
        content: String,
        isStreaming: Boolean,
        hasError: Boolean
    ) {
        messageDao.updateMessageStream(messageId, content, isStreaming, hasError)
    }

    override suspend fun deleteMessage(messageId: String) {
        messageDao.deleteMessageById(messageId)
    }

    override fun searchSessions(query: String): Flow<List<ChatSession>> {
        return sessionDao.searchSessions(query).map { list -> list.map { it.toDomain() } }
    }

    override fun searchMessages(query: String): Flow<List<Message>> {
        return messageDao.searchMessages(query).map { list -> list.map { it.toDomain() } }
    }

    override fun streamResponse(
        providerId: String,
        modelId: String,
        messages: List<Message>,
        systemPrompt: String?
    ): Flow<String> = flow {
        val chatMessages = mutableListOf<ChatMessageDto>()

        if (!systemPrompt.isNullOrBlank()) {
            chatMessages.add(ChatMessageDto(role = "system", content = systemPrompt))
        }

        messages.forEach { msg ->
            val role = when (msg.role) {
                MessageRole.USER -> "user"
                MessageRole.ASSISTANT -> "assistant"
                MessageRole.SYSTEM -> "system"
            }
            chatMessages.add(ChatMessageDto(role = role, content = msg.content))
        }

        val request = ChatCompletionRequest(
            model = modelId,
            messages = chatMessages,
            stream = true
        )

        val apiKey = keyStorage.getApiKey(providerId)
        val authHeader = if (apiKey.isNotBlank()) "Bearer $apiKey" else ""

        when (providerId.lowercase()) {
            "huggingface" -> {
                // Try Hugging Face Router endpoint first, fall back to direct model endpoint
                try {
                    val response = huggingFaceApi.createChatCompletionStream(authHeader, request)
                    val body = response.body()
                    if (response.isSuccessful && body != null) {
                        sseParser.parseOpenAiFormatStream(body).collect { emit(it) }
                        return@flow
                    }
                } catch (_: Exception) {}

                // Fallback to direct inference endpoint
                val lastPrompt = messages.lastOrNull { it.role == MessageRole.USER }?.content ?: ""
                val hfRequest = HuggingFaceInferenceRequest(
                    inputs = lastPrompt,
                    parameters = HuggingFaceParameters(maxNewTokens = 2048)
                )
                val response = huggingFaceApi.streamModelInference(modelId, authHeader, hfRequest)
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    sseParser.parseHuggingFaceStream(body).collect { emit(it) }
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Failed to stream from Hugging Face"
                    throw RuntimeException(errorMsg)
                }
            }

            "openrouter" -> {
                val response = openRouterApi.createChatCompletionStream(
                    authHeader = authHeader,
                    request = request
                )
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    sseParser.parseOpenAiFormatStream(body).collect { emit(it) }
                } else {
                    val err = response.errorBody()?.string() ?: "OpenRouter connection failed"
                    throw RuntimeException(err)
                }
            }

            "groq" -> {
                val response = groqApi.createChatCompletionStream(
                    authHeader = authHeader,
                    request = request
                )
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    sseParser.parseOpenAiFormatStream(body).collect { emit(it) }
                } else {
                    val err = response.errorBody()?.string() ?: "Groq connection failed"
                    throw RuntimeException(err)
                }
            }

            "together" -> {
                val response = togetherApi.createChatCompletionStream(
                    authHeader = authHeader,
                    request = request
                )
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    sseParser.parseOpenAiFormatStream(body).collect { emit(it) }
                } else {
                    val err = response.errorBody()?.string() ?: "Together AI connection failed"
                    throw RuntimeException(err)
                }
            }

            "ollama" -> {
                val response = ollamaApi.createChatCompletionStream(request = request)
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    sseParser.parseOpenAiFormatStream(body).collect { emit(it) }
                } else {
                    val err = response.errorBody()?.string() ?: "Ollama server not reachable. Ensure Ollama is running on port 11434."
                    throw RuntimeException(err)
                }
            }

            else -> {
                // Default OpenAI compatible format
                val response = openAiApi.createChatCompletionStream(
                    authHeader = authHeader,
                    request = request
                )
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    sseParser.parseOpenAiFormatStream(body).collect { emit(it) }
                } else {
                    val err = response.errorBody()?.string() ?: "Inference error from $providerId"
                    throw RuntimeException(err)
                }
            }
        }
    }
}
