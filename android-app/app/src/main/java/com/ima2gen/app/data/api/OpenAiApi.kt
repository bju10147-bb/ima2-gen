package com.ima2gen.app.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface OpenAiApi {
    @POST("images/generations")
    suspend fun generateImage(@Body request: OpenAiImageRequest): Response<OpenAiImageResponse>
}

@JsonClass(generateAdapter = true)
data class OpenAiImageRequest(
    val prompt: String,
    val model: String = "dall-e-3",
    val n: Int = 1,
    val quality: String = "standard", // standard or hd
    val size: String = "1024x1024",
    @Json(name = "response_format") val responseFormat: String = "b64_json"
)

@JsonClass(generateAdapter = true)
data class OpenAiImageResponse(
    val created: Long,
    val data: List<OpenAiImageData>
)

@JsonClass(generateAdapter = true)
data class OpenAiImageData(
    @Json(name = "b64_json") val b64Json: String? = null,
    val url: String? = null,
    @Json(name = "revised_prompt") val revisedPrompt: String? = null
)
