package com.aiuniverse.app.data.remote.api

import com.aiuniverse.app.data.remote.dto.ChatCompletionRequest
import com.aiuniverse.app.data.remote.dto.ChatCompletionResponse
import com.aiuniverse.app.data.remote.dto.OpenAiImageRequest
import com.aiuniverse.app.data.remote.dto.OpenAiImageResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Streaming

interface OpenAIApi {

    @Streaming
    @POST("chat/completions")
    suspend fun createChatCompletionStream(
        @Header("Authorization") authHeader: String,
        @Body request: ChatCompletionRequest
    ): Response<ResponseBody>

    @POST("chat/completions")
    suspend fun createChatCompletion(
        @Header("Authorization") authHeader: String,
        @Body request: ChatCompletionRequest
    ): Response<ChatCompletionResponse>

    @POST("images/generations")
    suspend fun generateImage(
        @Header("Authorization") authHeader: String,
        @Body request: OpenAiImageRequest
    ): Response<OpenAiImageResponse>
}
