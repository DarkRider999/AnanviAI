package com.aiuniverse.app.data.remote.interceptor

import com.aiuniverse.app.security.SecureKeyStorage
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Interceptor that injects provider-specific Bearer tokens and identification headers
 * at network level.
 */
class AuthHeaderInterceptor @Inject constructor(
    private val keyStorage: SecureKeyStorage
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val url = originalRequest.url.toString()

        val builder = originalRequest.newBuilder()

        val apiKey = when {
            url.contains("huggingface.co") -> keyStorage.getApiKey("huggingface")
            url.contains("openrouter.ai") -> {
                builder.header("HTTP-Referer", "https://aiuniverse.app")
                builder.header("X-Title", "AI Universe Android")
                keyStorage.getApiKey("openrouter")
            }
            url.contains("api.groq.com") -> keyStorage.getApiKey("groq")
            url.contains("api.together.xyz") -> keyStorage.getApiKey("together")
            url.contains("api.openai.com") -> keyStorage.getApiKey("openai")
            else -> ""
        }

        if (apiKey.isNotBlank() && originalRequest.header("Authorization") == null) {
            builder.header("Authorization", "Bearer $apiKey")
        }

        builder.header("Accept", "text/event-stream, application/json")

        return chain.proceed(builder.build())
    }
}
