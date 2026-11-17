package com.aiuniverse.app.domain.usecase

import com.aiuniverse.app.domain.model.Message
import com.aiuniverse.app.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMessagesUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    operator fun invoke(sessionId: String): Flow<List<Message>> {
        return repository.getMessages(sessionId)
    }

    suspend fun deleteMessage(messageId: String) {
        repository.deleteMessage(messageId)
    }
}
