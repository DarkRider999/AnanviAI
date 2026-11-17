package com.aiuniverse.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aiuniverse.app.domain.model.AIProvider

@Entity(tableName = "ai_providers")
data class AIProviderEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val baseUrl: String,
    val isConfigured: Boolean,
    val requiresApiKey: Boolean,
    val defaultModel: String,
    val websiteUrl: String
) {
    fun toDomain(): AIProvider = AIProvider(
        id = id,
        name = name,
        description = description,
        baseUrl = baseUrl,
        isConfigured = isConfigured,
        requiresApiKey = requiresApiKey,
        defaultModel = defaultModel,
        websiteUrl = websiteUrl
    )

    companion object {
        fun fromDomain(p: AIProvider): AIProviderEntity = AIProviderEntity(
            id = p.id,
            name = p.name,
            description = p.description,
            baseUrl = p.baseUrl,
            isConfigured = p.isConfigured,
            requiresApiKey = p.requiresApiKey,
            defaultModel = p.defaultModel,
            websiteUrl = p.websiteUrl
        )
    }
}
