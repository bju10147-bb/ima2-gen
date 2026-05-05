package com.ima2gen.app.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface OpenAiApi {
    @POST("responses")
    suspend fun createResponse(@Body request: ResponsesImageRequest): Response<ResponsesImageResponse>
}

@JsonClass(generateAdapter = true)
data class ResponsesImageRequest(
    val model: String,
    val input: List<ResponsesInputMessage>,
    val tools: List<ResponsesTool>,
    @Json(name = "tool_choice") val toolChoice: String = "required",
    val reasoning: ResponsesReasoning? = null,
    val stream: Boolean = false,
)

@JsonClass(generateAdapter = true)
data class ResponsesInputMessage(
    val role: String,
    val content: List<ResponsesContentItem>,
)

@JsonClass(generateAdapter = true)
data class ResponsesContentItem(
    val type: String,
    val text: String? = null,
    @Json(name = "image_url") val imageUrl: String? = null,
    val detail: String? = null,
)

@JsonClass(generateAdapter = true)
data class ResponsesTool(
    val type: String,
    val quality: String? = null,
    val size: String? = null,
    val moderation: String? = null,
    @Json(name = "output_format") val outputFormat: String? = null,
    val background: String? = null,
    @Json(name = "partial_images") val partialImages: Int? = null,
)

@JsonClass(generateAdapter = true)
data class ResponsesReasoning(
    val effort: String,
)

@JsonClass(generateAdapter = true)
data class ResponsesImageResponse(
    val id: String? = null,
    val output: List<ResponsesOutputItem> = emptyList(),
    val usage: ResponsesUsage? = null,
)

@JsonClass(generateAdapter = true)
data class ResponsesUsage(
    @Json(name = "total_tokens") val totalTokens: Long? = null,
    @Json(name = "prompt_tokens") val promptTokens: Long? = null,
    @Json(name = "completion_tokens") val completionTokens: Long? = null
)

@JsonClass(generateAdapter = true)
data class ResponsesOutputItem(
    val type: String,
    val status: String? = null,
    val result: String? = null,
    @Json(name = "revised_prompt") val revisedPrompt: String? = null,
)
