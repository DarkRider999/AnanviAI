package com.aiuniverse.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aiuniverse.app.domain.model.ChatSession

@Entity(tableName = "chat_sessions")
data class ChatSessionEntity(
    @PrimaryKey val id: String,
    val title: String,
    val providerId: String,
    val modelId: String,
    val createdAt: Long,
    val updatedAt: Long,
    val systemPrompt: String?,
    val isPinned: Boolean = false,
    val messageCount: Int = 0
) {
    fun toDomain(): ChatSession = ChatSession(
        id = id,
        title = title,
        providerId = providerId,
        modelId = modelId,
        createdAt = createdAt,
        updatedAt = updatedAt,
        systemPrompt = systemPrompt,
        isPinned = isPinned,
        messageCount = messageCount
    )

    companion object {
        fun fromDomain(session: ChatSession): ChatSessionEntity = ChatSessionEntity(
            id = session.id,
            title = session.title,
            providerId = session.providerId,
            modelId = session.modelId,
            createdAt = session.createdAt,
            updatedAt = session.updatedAt,
            systemPrompt = session.systemPrompt,
            isPinned = session.isPinned,
            messageCount = session.messageCount
        )
    }
}
