package com.aiuniverse.app.data.remote.api

import com.aiuniverse.app.data.remote.dto.ChatCompletionRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
 * Shared streaming client for providers that implement the OpenAI-compatible
 * Chat Completions contract. The absolute URL is selected from the provider
 * registry; credentials are supplied per request and are never persisted here.
 */
interface GenericOpenAiCompatibleApi {
    @Streaming
    @POST
    suspend fun createChatCompletionStream(
        @Url url: String,
        @Header("Authorization") authorization: String,
        @Body request: ChatCompletionRequest
    ): Response<ResponseBody>
}
