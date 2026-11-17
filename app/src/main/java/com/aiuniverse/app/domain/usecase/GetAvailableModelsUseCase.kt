package com.aiuniverse.app.domain.usecase

import com.aiuniverse.app.domain.model.AIModel
import com.aiuniverse.app.domain.repository.AIProviderRepository
import javax.inject.Inject

class GetAvailableModelsUseCase @Inject constructor(
    private val repository: AIProviderRepository
) {
    suspend fun execute(providerId: String? = null): List<AIModel> {
        return if (providerId != null) {
            repository.getAvailableModels(providerId)
        } else {
            repository.getAllAvailableModels()
        }
    }
}
