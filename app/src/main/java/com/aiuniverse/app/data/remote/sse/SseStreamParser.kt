package com.aiuniverse.app.data.remote.sse

import com.aiuniverse.app.common.Constants
import com.aiuniverse.app.data.remote.dto.ChatCompletionResponse
import com.aiuniverse.app.data.remote.dto.HuggingFaceInferenceResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Robust SSE (Server-Sent Events) parser that transforms raw chunked response streams
 * into continuous text delta tokens for real-time ChatGPT-style typing animations.
 */
class SseStreamParser(private val gson: Gson = Gson()) {

    fun parseOpenAiFormatStream(responseBody: ResponseBody): Flow<String> = flow {
        val reader = BufferedReader(InputStreamReader(responseBody.byteStream(), Charsets.UTF_8))
        try {
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val currentLine = line?.trim() ?: continue
                if (currentLine.isEmpty() || currentLine.startsWith(":")) {
                    // Keep-alive or comment ping
                    continue
                }

                if (currentLine.startsWith(Constants.SSE_DATA_PREFIX)) {
                    val jsonPayload = currentLine.removePrefix(Constants.SSE_DATA_PREFIX).trim()
                    if (jsonPayload == Constants.SSE_DONE) {
                        break
                    }

                    try {
                        val chunk = gson.fromJson(jsonPayload, ChatCompletionResponse::class.java)
                        val delta = chunk.choices.firstOrNull()?.delta?.content
                        if (!delta.isNullOrEmpty()) {
                            emit(delta)
                        }
                    } catch (e: Exception) {
                        // Skip malformed chunk or partial framing
                    }
                }
            }
        } finally {
            try {
                reader.close()
                responseBody.close()
            } catch (_: Exception) {}
        }
    }

    fun parseHuggingFaceStream(responseBody: ResponseBody): Flow<String> = flow {
        val reader = BufferedReader(InputStreamReader(responseBody.byteStream(), Charsets.UTF_8))
        try {
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val currentLine = line?.trim() ?: continue
                if (currentLine.isEmpty() || currentLine.startsWith(":")) continue

                if (currentLine.startsWith(Constants.SSE_DATA_PREFIX)) {
                    val jsonPayload = currentLine.removePrefix(Constants.SSE_DATA_PREFIX).trim()
                    if (jsonPayload == Constants.SSE_DONE) break

                    try {
                        val chunk = gson.fromJson(jsonPayload, HuggingFaceInferenceResponse::class.java)
                        val tokenText = chunk.token?.text
                        if (!tokenText.isNullOrEmpty()) {
                            emit(tokenText)
                        } else if (!chunk.generatedText.isNullOrEmpty()) {
                            emit(chunk.generatedText)
                        }
                    } catch (e: Exception) {
                        // If direct string chunk
                    }
                }
            }
        } finally {
            try {
                reader.close()
                responseBody.close()
            } catch (_: Exception) {}
        }
    }
}
