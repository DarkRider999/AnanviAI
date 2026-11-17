package com.aiuniverse.app.data.remote.api

import com.aiuniverse.app.data.remote.dto.ChatCompletionRequest
import com.aiuniverse.app.data.remote.dto.HuggingFaceInferenceRequest
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Streaming

interface HuggingFaceApi {

    /**
     * OpenAI-compatible router endpoint for Hugging Face Inference API.
     * URL: https://router.huggingface.co/hf-inference/v1/chat/completions
     */
    @Streaming
    @POST("chat/completions")
    suspend fun createChatCompletionStream(
        @Header("Authorization") authHeader: String,
        @Body request: ChatCompletionRequest
    ): Response<ResponseBody>

    /**
     * Direct Server-Sent Events inference endpoint for specific Hugging Face models.
     * URL: https://api-inference.huggingface.co/models/{modelId}
     */
    @Streaming
    @POST("models/{modelId}")
    suspend fun streamModelInference(
        @Path(value = "modelId", encoded = true) modelId: String,
        @Header("Authorization") authHeader: String,
        @Body request: HuggingFaceInferenceRequest
    ): Response<ResponseBody>

    /**
     * Diffusion Image Generation endpoint on Hugging Face.
     * e.g. stabilityai/stable-diffusion-xl-base-1.0 or black-forest-labs/FLUX.1-schnell
     */
    @POST("models/{modelId}")
    suspend fun generateImage(
        @Path(value = "modelId", encoded = true) modelId: String,
        @Header("Authorization") authHeader: String,
        @Body body: RequestBody
    ): Response<ResponseBody>
}
