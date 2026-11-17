package com.aiuniverse.app.domain.usecase

import com.aiuniverse.app.domain.model.AIModel
import com.aiuniverse.app.domain.model.Message
import com.aiuniverse.app.domain.model.MessageRole
import com.aiuniverse.app.domain.model.ModelComparisonResult
import com.aiuniverse.app.domain.repository.ChatRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList
import javax.inject.Inject
import kotlin.system.measureTimeMillis

class CompareModelsUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend fun execute(
        prompt: String,
        modelA: AIModel,
        modelB: AIModel
    ): ModelComparisonResult = coroutineScope {
        val userMsg = Message(
            id = "compare_user",
            sessionId = "comparison",
            role = MessageRole.USER,
            content = prompt
        )

        var respA = ""
        var respB = ""

        val timeA = measureTimeMillis {
            val deferredA = async {
                try {
                    val tokens = chatRepository.streamResponse(
                        providerId = modelA.providerId,
                        modelId = modelA.id,
                        messages = listOf(userMsg),
                        systemPrompt = null
                    ).toList()
                    tokens.joinToString("")
                } catch (e: Exception) {
                    "Model A error: ${e.localizedMessage}"
                }
            }
            respA = deferredA.await()
        }

        val timeB = measureTimeMillis {
            val deferredB = async {
                try {
                    val tokens = chatRepository.streamResponse(
                        providerId = modelB.providerId,
                        modelId = modelB.id,
                        messages = listOf(userMsg),
                        systemPrompt = null
                    ).toList()
                    tokens.joinToString("")
                } catch (e: Exception) {
                    "Model B error: ${e.localizedMessage}"
                }
            }
            respB = deferredB.await()
        }

        ModelComparisonResult(
            prompt = prompt,
            modelA = modelA,
            modelB = modelB,
            responseA = respA,
            responseB = respB,
            latencyMsA = timeA,
            latencyMsB = timeB,
            isGenerating = false
        )
    }
}
