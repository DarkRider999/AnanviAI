package com.aiuniverse.app.domain.repository

import com.aiuniverse.app.domain.model.ChatSession
import com.aiuniverse.app.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getSessions(): Flow<List<ChatSession>>
    fun getSession(sessionId: String): Flow<ChatSession?>
    suspend fun getSessionOnce(sessionId: String): ChatSession?
    fun getMessages(sessionId: String): Flow<List<Message>>
    suspend fun createSession(title: String, providerId: String, modelId: String, systemPrompt: String? = null): ChatSession
    suspend fun updateSessionTitle(sessionId: String, title: String)
    suspend fun pinSession(sessionId: String, isPinned: Boolean)
    suspend fun deleteSession(sessionId: String)
    suspend fun saveMessage(message: Message)
    suspend fun updateMessageStream(messageId: String, content: String, isStreaming: Boolean, hasError: Boolean)
    suspend fun deleteMessage(messageId: String)
    fun searchSessions(query: String): Flow<List<ChatSession>>
    fun searchMessages(query: String): Flow<List<Message>>

    /**
     * Sends message with full conversation context and returns a real-time reactive streaming Flow.
     */
    fun streamResponse(
        providerId: String,
        modelId: String,
        messages: List<Message>,
        systemPrompt: String?
    ): Flow<String>
}
