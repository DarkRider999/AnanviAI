package com.aiuniverse.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class HuggingFaceInferenceRequest(
    @SerializedName("inputs") val inputs: String,
    @SerializedName("parameters") val parameters: HuggingFaceParameters? = HuggingFaceParameters(),
    @SerializedName("options") val options: HuggingFaceOptions? = HuggingFaceOptions()
)

data class HuggingFaceParameters(
    @SerializedName("max_new_tokens") val maxNewTokens: Int = 2048,
    @SerializedName("temperature") val temperature: Float = 0.7f,
    @SerializedName("top_p") val topP: Float = 0.9f,
    @SerializedName("return_full_text") val returnFullText: Boolean = false
)

data class HuggingFaceOptions(
    @SerializedName("wait_for_model") val waitForModel: Boolean = true,
    @SerializedName("use_cache") val useCache: Boolean = false
)

data class HuggingFaceInferenceResponse(
    @SerializedName("generated_text") val generatedText: String? = null,
    @SerializedName("token") val token: HuggingFaceStreamToken? = null,
    @SerializedName("error") val error: String? = null
)

data class HuggingFaceStreamToken(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("text") val text: String? = null,
    @SerializedName("special") val special: Boolean = false
)
