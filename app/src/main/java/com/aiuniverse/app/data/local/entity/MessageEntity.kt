package com.aiuniverse.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.aiuniverse.app.domain.model.MediaType
import com.aiuniverse.app.domain.model.Message
import com.aiuniverse.app.domain.model.MessageRole

@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = ChatSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["sessionId"])]
)
data class MessageEntity(
    @PrimaryKey val id: String,
    val sessionId: String,
    val role: MessageRole,
    val content: String,
    val timestamp: Long,
    val modelName: String?,
    val tokenCount: Int,
    val isStreaming: Boolean,
    val hasError: Boolean,
    val mediaUri: String?,
    val mediaType: MediaType
) {
    fun toDomain(): Message = Message(
        id = id,
        sessionId = sessionId,
        role = role,
        content = content,
        timestamp = timestamp,
        modelName = modelName,
        tokenCount = tokenCount,
        isStreaming = isStreaming,
        hasError = hasError,
        mediaUri = mediaUri,
        mediaType = mediaType
    )

    companion object {
        fun fromDomain(msg: Message): MessageEntity = MessageEntity(
            id = msg.id,
            sessionId = msg.sessionId,
            role = msg.role,
            content = msg.content,
            timestamp = msg.timestamp,
            modelName = msg.modelName,
            tokenCount = msg.tokenCount,
            isStreaming = msg.isStreaming,
            hasError = msg.hasError,
            mediaUri = msg.mediaUri,
            mediaType = msg.mediaType
        )
    }
}
