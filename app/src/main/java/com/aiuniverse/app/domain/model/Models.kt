package com.aiuniverse.app.domain.model

enum class MessageRole {
    USER,
    ASSISTANT,
    SYSTEM
}

enum class MediaType {
    NONE,
    IMAGE,
    PDF,
    AUDIO
}

data class ChatSession(
    val id: String,
    val title: String,
    val providerId: String,
    val modelId: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val systemPrompt: String? = null,
    val isPinned: Boolean = false,
    val messageCount: Int = 0
)

data class Message(
    val id: String,
    val sessionId: String,
    val role: MessageRole,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val modelName: String? = null,
    val tokenCount: Int = 0,
    val isStreaming: Boolean = false,
    val hasError: Boolean = false,
    val mediaUri: String? = null,
    val mediaType: MediaType = MediaType.NONE
)

data class AIProvider(
    val id: String,
    val name: String,
    val description: String,
    val baseUrl: String,
    val isConfigured: Boolean = false,
    val requiresApiKey: Boolean = true,
    val defaultModel: String,
    val websiteUrl: String = ""
)

data class AIModel(
    val id: String,
    val name: String,
    val providerId: String,
    val description: String,
    val contextWindow: Int = 8192,
    val isFree: Boolean = false,
    val category: String = "General",
    val isMultimodal: Boolean = false
)

data class ModelComparisonResult(
    val prompt: String,
    val modelA: AIModel,
    val modelB: AIModel,
    val responseA: String,
    val responseB: String,
    val latencyMsA: Long = 0,
    val latencyMsB: Long = 0,
    val isGenerating: Boolean = false
)

data class ImageGenRequest(
    val prompt: String,
    val negativePrompt: String = "",
    val model: String = "stabilityai/stable-diffusion-xl-base-1.0",
    val width: Int = 1024,
    val height: Int = 1024,
    val numInferenceSteps: Int = 30,
    val guidanceScale: Float = 7.5f
)

data class ImageGenResult(
    val id: String,
    val prompt: String,
    val imageUrl: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class AiToolType(val title: String, val description: String, val promptTemplate: String) {
    SUMMARIZE(
        title = "Summarize Text",
        description = "Create clear, concise summaries from long articles, papers or documents.",
        promptTemplate = "Please provide a comprehensive yet concise summary of the following text with bullet points for key takeaways:\n\n"
    ),
    REWRITE(
        title = "Rewrite & Polish",
        description = "Improve tone, clarity, and grammatical structure of your draft.",
        promptTemplate = "Rewrite the following text to make it professional, engaging, clear, and grammatically flawless:\n\n"
    ),
    GENERATE_CODE(
        title = "Generate Code",
        description = "Generate clean, production-grade code with comments and unit tests.",
        promptTemplate = "Write clean, idiomatic, well-documented code for the following task:\n\n"
    ),
    EXPLAIN_CODE(
        title = "Explain Code",
        description = "Break down complex algorithms, codebases, or stack traces step-by-step.",
        promptTemplate = "Explain this code in detail: explain its logic, time/space complexity, potential bugs, and optimization opportunities:\n\n"
    ),
    TRANSLATE(
        title = "Translate",
        description = "Translate nuances accurately into any desired language.",
        promptTemplate = "Translate the following text accurately, preserving its cultural tone and technical context:\n\n"
    ),
    EMAIL_WRITER(
        title = "Email Writer",
        description = "Draft professional, persuasive, and crisp emails in seconds.",
        promptTemplate = "Draft a professional, clear, and compelling email based on these points:\n\n"
    ),
    SOCIAL_POST(
        title = "Social Media Post",
        description = "Generate viral posts for X (Twitter), LinkedIn, Threads, or Instagram.",
        promptTemplate = "Craft high-engagement social media posts (with hashtags and hooks) based on:\n\n"
    )
}

data class UserProfile(
    val id: String = "user_default",
    val name: String = "Cosmic Explorer",
    val email: String = "user@aiuniverse.local",
    val avatarUri: String? = null,
    val customSystemPrompt: String = "You are AI Universe, a hyper-capable, versatile, and thoughtful assistant.",
    val totalTokensUsed: Long = 0L,
    val favoriteModelId: String = "meta-llama/Llama-3.1-8B-Instruct"
)

data class AppSettings(
    val themeMode: String = "SYSTEM", // SYSTEM, DARK, LIGHT
    val streamResponses: Boolean = true,
    val hapticFeedback: Boolean = true,
    val soundEffects: Boolean = false,
    val defaultProviderId: String = "huggingface",
    val defaultModelId: String = "meta-llama/Llama-3.1-8B-Instruct",
    val temperature: Float = 0.7f,
    val topP: Float = 0.9f
)
