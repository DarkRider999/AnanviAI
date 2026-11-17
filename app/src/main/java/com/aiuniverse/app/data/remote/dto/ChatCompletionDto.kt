package com.aiuniverse.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ChatCompletionRequest(
    @SerializedName("model") val model: String,
    @SerializedName("messages") val messages: List<ChatMessageDto>,
    @SerializedName("stream") val stream: Boolean = true,
    @SerializedName("temperature") val temperature: Float? = 0.7f,
    @SerializedName("max_tokens") val maxTokens: Int? = 4096,
    @SerializedName("top_p") val topP: Float? = 0.9f
)

data class ChatMessageDto(
    @SerializedName("role") val role: String,
    @SerializedName("content") val content: String
)

data class ChatCompletionResponse(
    @SerializedName("id") val id: String? = null,
    @SerializedName("model") val model: String? = null,
    @SerializedName("choices") val choices: List<ChoiceDto> = emptyList(),
    @SerializedName("usage") val usage: UsageDto? = null
)

data class ChoiceDto(
    @SerializedName("index") val index: Int = 0,
    @SerializedName("message") val message: ChatMessageDto? = null,
    @SerializedName("delta") val delta: DeltaDto? = null,
    @SerializedName("finish_reason") val finishReason: String? = null
)

data class DeltaDto(
    @SerializedName("role") val role: String? = null,
    @SerializedName("content") val content: String? = null
)

data class UsageDto(
    @SerializedName("prompt_tokens") val promptTokens: Int = 0,
    @SerializedName("completion_tokens") val completionTokens: Int = 0,
    @SerializedName("total_tokens") val totalTokens: Int = 0
)
