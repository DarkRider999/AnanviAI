package com.aiuniverse.app.domain.usecase

import com.aiuniverse.app.domain.model.ChatSession
import com.aiuniverse.app.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChatSessionsUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    operator fun invoke(): Flow<List<ChatSession>> = repository.getSessions()

    fun search(query: String): Flow<List<ChatSession>> = repository.searchSessions(query)
}
