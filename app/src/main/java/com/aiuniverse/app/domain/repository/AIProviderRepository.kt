package com.aiuniverse.app.domain.repository

import com.aiuniverse.app.domain.model.AIModel
import com.aiuniverse.app.domain.model.AIProvider
import kotlinx.coroutines.flow.Flow

interface AIProviderRepository {
    fun getProviders(): Flow<List<AIProvider>>
    fun getProvider(id: String): Flow<AIProvider?>
    suspend fun getAvailableModels(providerId: String): List<AIModel>
    suspend fun getAllAvailableModels(): List<AIModel>
    fun getApiKey(providerId: String): String
    suspend fun saveApiKey(providerId: String, apiKey: String)
    suspend fun removeApiKey(providerId: String)
    suspend fun testConnection(providerId: String): Boolean
}
