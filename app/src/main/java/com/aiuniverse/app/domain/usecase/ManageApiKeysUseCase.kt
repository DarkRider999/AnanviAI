package com.aiuniverse.app.domain.usecase

import com.aiuniverse.app.domain.model.AIProvider
import com.aiuniverse.app.domain.repository.AIProviderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ManageApiKeysUseCase @Inject constructor(
    private val repository: AIProviderRepository
) {
    fun getProviders(): Flow<List<AIProvider>> = repository.getProviders()

    fun getApiKey(providerId: String): String = repository.getApiKey(providerId)

    suspend fun saveApiKey(providerId: String, apiKey: String) {
        repository.saveApiKey(providerId, apiKey)
    }

    suspend fun removeApiKey(providerId: String) {
        repository.removeApiKey(providerId)
    }

    suspend fun testConnection(providerId: String): Boolean {
        return repository.testConnection(providerId)
    }
}
