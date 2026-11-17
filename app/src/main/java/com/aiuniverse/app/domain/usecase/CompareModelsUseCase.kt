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

        // Launch both requests before awaiting either one so the comparison is genuinely parallel.
        val deferredA = async {
            var responseText = ""
            val elapsed = measureTimeMillis {
                responseText = try {
                    chatRepository.streamResponse(
                        providerId = modelA.providerId,
                        modelId = modelA.id,
                        messages = listOf(userMsg),
                        systemPrompt = null
                    ).toList().joinToString("")
                } catch (e: Exception) {
                    "Model A error: ${e.localizedMessage?.take(500) ?: "Request failed"}"
                }
            }
            responseText to elapsed
        }

        val deferredB = async {
            var responseText = ""
            val elapsed = measureTimeMillis {
                responseText = try {
                    chatRepository.streamResponse(
                        providerId = modelB.providerId,
                        modelId = modelB.id,
                        messages = listOf(userMsg),
                        systemPrompt = null
                    ).toList().joinToString("")
                } catch (e: Exception) {
                    "Model B error: ${e.localizedMessage?.take(500) ?: "Request failed"}"
                }
            }
            responseText to elapsed
        }

        val (respA, timeA) = deferredA.await()
        val (respB, timeB) = deferredB.await()

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
