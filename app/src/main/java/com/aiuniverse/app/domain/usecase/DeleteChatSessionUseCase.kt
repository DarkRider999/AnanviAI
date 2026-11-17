package com.aiuniverse.app.domain.usecase

import com.aiuniverse.app.domain.repository.ChatRepository
import javax.inject.Inject

class DeleteChatSessionUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(sessionId: String) {
        repository.deleteSession(sessionId)
    }
}
