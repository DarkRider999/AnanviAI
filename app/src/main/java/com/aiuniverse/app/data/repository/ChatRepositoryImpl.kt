package com.aiuniverse.app.data.repository

import com.aiuniverse.app.data.local.dao.ChatSessionDao
import com.aiuniverse.app.data.local.dao.MessageDao
import com.aiuniverse.app.data.local.entity.ChatSessionEntity
import com.aiuniverse.app.data.local.entity.MessageEntity
import com.aiuniverse.app.data.remote.api.GenericOpenAiCompatibleApi
import com.aiuniverse.app.data.remote.api.GroqApi
import com.aiuniverse.app.data.remote.api.HuggingFaceChatApi
import com.aiuniverse.app.data.remote.api.OllamaApi
import com.aiuniverse.app.data.remote.api.OpenAIApi
import com.aiuniverse.app.data.remote.api.OpenRouterApi
import com.aiuniverse.app.data.remote.api.TogetherApi
import com.aiuniverse.app.data.remote.dto.ChatCompletionRequest
import com.aiuniverse.app.data.remote.dto.ChatMessageDto
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
    private val huggingFaceApi: HuggingFaceChatApi,
    private val openRouterApi: OpenRouterApi,
    private val groqApi: GroqApi,
    private val openAiApi: OpenAIApi,
    private val ollamaApi: OllamaApi,
    private val togetherApi: TogetherApi,
    private val genericApi: GenericOpenAiCompatibleApi,
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
                if (apiKey.isBlank()) {
                    throw IllegalStateException(
                        "Add a Hugging Face access token in API Keys before using Hugging Face models."
                    )
                }

                // Hugging Face's current OpenAI-compatible Inference Providers route.
                // Do not fall back to the retired api-inference.huggingface.co/models path:
                // it is not a chat-completions endpoint and can return an HTML error page.
                val response = huggingFaceApi.createChatCompletionStream(
                    authHeader = authHeader,
                    request = request
                )
                val body = response.body()

                if (response.isSuccessful && body != null) {
                    val contentType = body.contentType()?.toString().orEmpty()
                    if (!contentType.contains("text/event-stream", ignoreCase = true)) {
                        val unexpectedBody = body.string()
                        val isHtml = unexpectedBody.contains("<html", ignoreCase = true) ||
                            unexpectedBody.contains("<!doctype html", ignoreCase = true)
                        val detail = if (isHtml) {
                            "Hugging Face returned an HTML page instead of model output. " +
                                "This usually indicates a proxy, routing, or endpoint error."
                        } else {
                            "Expected a streaming response but received Content-Type: " +
                                (contentType.ifBlank { "unknown" }) + ". " +
                                unexpectedBody.take(300)
                        }
                        throw IllegalStateException(detail)
                    }
                    var receivedAnyText = false
                    sseParser.parseOpenAiFormatStream(body).collect {
                        receivedAnyText = true
                        emit(it)
                    }
                    if (!receivedAnyText) {
                        throw IllegalStateException(
                            "Hugging Face returned an empty stream. Check model availability, " +
                                "Inference Providers permissions, and token credits."
                        )
                    }
                } else {
                    val errorBody = response.errorBody()?.string().orEmpty()
                    val isHtml = errorBody.contains("<html", ignoreCase = true) ||
                        errorBody.contains("<!doctype html", ignoreCase = true)
                    val detail = when {
                        isHtml -> "The server returned an HTML page instead of an API response. " +
                            "Check the model/provider availability and Hugging Face access token."
                        errorBody.isNotBlank() -> errorBody.take(500)
                        else -> "No error details were returned."
                    }
                    throw IllegalStateException(
                        "Hugging Face request failed (HTTP ${response.code()}): $detail"
                    )
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

            "gemini", "nvidia", "sambanova", "cohere", "cloudflare" -> {
                if (apiKey.isBlank()) {
                    throw IllegalStateException(
                        "Add an API key for ${providerId} in Settings > API Keys."
                    )
                }

                val cloudflareParts = if (providerId == "cloudflare") apiKey.split("|", limit = 2).map { it.trim() } else emptyList()
                if (providerId == "cloudflare" && (cloudflareParts.size != 2 ||
                        cloudflareParts[0].isBlank() || cloudflareParts[1].isBlank())
                ) {
                    throw IllegalStateException(
                        "Cloudflare key format: enter ACCOUNT_ID|API_TOKEN in Settings > API Keys. " +
                            "The value is stored locally in encrypted preferences."
                    )
                }

                val endpoint = when (providerId.lowercase()) {
                    "gemini" -> "https://generativelanguage.googleapis.com/v1beta/openai/chat/completions"
                    "nvidia" -> "https://integrate.api.nvidia.com/v1/chat/completions"
                    "sambanova" -> "https://api.sambanova.ai/v1/chat/completions"
                    "cohere" -> "https://api.cohere.ai/compatibility/v1/chat/completions"
                    "cloudflare" -> "https://api.cloudflare.com/client/v4/accounts/${cloudflareParts[0]}/ai/v1/chat/completions"
                    "zai" -> "https://api.z.ai/api/paas/v4/chat/completions"
                    else -> error("Unsupported provider: $providerId")
                }
                val providerAuthorization = if (providerId == "cloudflare") {
                    "Bearer ${cloudflareParts[1]}"
                } else authHeader

                // Keep compatibility with the old Gemini model ID saved by v1.0.0.
                val compatibleRequest = if (
                    providerId == "gemini" &&
                    request.model == "google/gemini-flash-1.5"
                ) {
                    request.copy(model = "gemini-3.8-flash")
                } else request

                val response = genericApi.createChatCompletionStream(
                    url = endpoint,
                    authorization = providerAuthorization,
                    request = compatibleRequest
                )
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    val contentType = body.contentType()?.toString().orEmpty()
                    if (!contentType.contains("text/event-stream", ignoreCase = true)) {
                        val unexpectedBody = body.string()
                        val isHtml = unexpectedBody.contains("<html", ignoreCase = true) ||
                            unexpectedBody.contains("<!doctype html", ignoreCase = true)
                        val detail = if (isHtml) {
                            "The provider returned an HTML page instead of an API response. " +
                                "Check the endpoint, account access, and network connection."
                        } else {
                            "Expected a streaming response but received Content-Type: " +
                                (contentType.ifBlank { "unknown" }) + ". " +
                                unexpectedBody.take(300)
                        }
                        throw IllegalStateException(detail)
                    }
                    var receivedText = false
                    sseParser.parseOpenAiFormatStream(body).collect {
                        receivedText = true
                        emit(it)
                    }
                    if (!receivedText) {
                        throw IllegalStateException(
                            "$providerId returned an empty response. Check model availability and free-tier quota."
                        )
                    }
                } else {
                    val errorBody = response.errorBody()?.string().orEmpty()
                    val detail = when {
                        errorBody.contains("<html", ignoreCase = true) ||
                            errorBody.contains("<!doctype html", ignoreCase = true) ->
                            "Provider returned an HTML error page. Check endpoint and account access."
                        errorBody.isNotBlank() -> errorBody.take(500)
                        else -> "No error details were returned."
                    }
                    throw IllegalStateException(
                        "$providerId request failed (HTTP ${response.code()}): $detail"
                    )
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
