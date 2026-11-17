package com.aiuniverse.app.domain.repository

import com.aiuniverse.app.domain.model.ImageGenRequest
import com.aiuniverse.app.domain.model.ImageGenResult
import kotlinx.coroutines.flow.Flow

interface ImageGenerationRepository {
    suspend fun generateImage(request: ImageGenRequest): ImageGenResult
    fun getRecentGenerations(): Flow<List<ImageGenResult>>
    suspend fun saveGeneration(result: ImageGenResult)
}
