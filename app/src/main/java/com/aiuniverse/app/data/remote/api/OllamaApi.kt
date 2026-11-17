package com.aiuniverse.app.data.remote.api

import com.aiuniverse.app.data.remote.dto.ChatCompletionRequest
import com.aiuniverse.app.data.remote.dto.ChatCompletionResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Streaming

interface OllamaApi {

    @Streaming
    @POST("v1/chat/completions")
    suspend fun createChatCompletionStream(
        @Body request: ChatCompletionRequest
    ): Response<ResponseBody>

    @POST("v1/chat/completions")
    suspend fun createChatCompletion(
        @Body request: ChatCompletionRequest
    ): Response<ChatCompletionResponse>
}
