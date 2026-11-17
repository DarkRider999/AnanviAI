package com.aiuniverse.app.domain.usecase

import com.aiuniverse.app.domain.model.ImageGenRequest
import com.aiuniverse.app.domain.model.ImageGenResult
import com.aiuniverse.app.domain.repository.ImageGenerationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GenerateImageUseCase @Inject constructor(
    private val repository: ImageGenerationRepository
) {
    suspend fun execute(request: ImageGenRequest): ImageGenResult {
        return repository.generateImage(request)
    }

    fun getRecentGenerations(): Flow<List<ImageGenResult>> {
        return repository.getRecentGenerations()
    }
}
