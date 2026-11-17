package com.aiuniverse.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class OpenAiImageRequest(
    @SerializedName("prompt") val prompt: String,
    @SerializedName("model") val model: String = "dall-e-3",
    @SerializedName("n") val n: Int = 1,
    @SerializedName("size") val size: String = "1024x1024",
    @SerializedName("response_format") val responseFormat: String = "url"
)

data class OpenAiImageResponse(
    @SerializedName("created") val created: Long = 0,
    @SerializedName("data") val data: List<ImageDataItem> = emptyList()
)

data class ImageDataItem(
    @SerializedName("url") val url: String? = null,
    @SerializedName("b64_json") val b64Json: String? = null
)
