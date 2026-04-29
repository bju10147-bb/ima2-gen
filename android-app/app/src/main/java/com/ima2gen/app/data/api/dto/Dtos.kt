package com.ima2gen.app.data.api.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// ── Health ──

@JsonClass(generateAdapter = true)
data class HealthResponse(
    val ok: Boolean,
    val version: String? = null,
    val provider: String? = null,
    @Json(name = "uptimeSec") val uptimeSec: Int? = null,
    @Json(name = "activeJobs") val activeJobs: Int? = null,
    val pid: Int? = null,
    @Json(name = "startedAt") val startedAt: Long? = null,
)

@JsonClass(generateAdapter = true)
data class ProvidersResponse(
    @Json(name = "apiKey") val apiKey: Boolean = false,
    val oauth: Boolean = false,
    @Json(name = "apiKeyDisabled") val apiKeyDisabled: Boolean = true,
    @Json(name = "oauthUrl") val oauthUrl: String? = null,
)

@JsonClass(generateAdapter = true)
data class BillingResponse(
    @Json(name = "apiKeyValid") val apiKeyValid: Boolean = false,
    @Json(name = "apiKeySource") val apiKeySource: String? = null,
)

@JsonClass(generateAdapter = true)
data class InflightResponse(
    val jobs: List<InflightJob> = emptyList(),
    @Json(name = "terminalJobs") val terminalJobs: List<InflightJob>? = null,
)

@JsonClass(generateAdapter = true)
data class InflightJob(
    @Json(name = "requestId") val requestId: String,
    val kind: String? = null,
    val prompt: String? = null,
    @Json(name = "startedAt") val startedAt: Long? = null,
)

// ── Generate ──

@JsonClass(generateAdapter = true)
data class GenerateRequest(
    val prompt: String,
    val quality: String = "medium",
    val size: String = "1024x1024",
    val format: String = "png",
    val moderation: String = "low",
    val model: String? = null,
    val n: Int = 1,
    val references: List<String> = emptyList(),
    val mode: String = "auto",
    @Json(name = "sessionId") val sessionId: String? = null,
    @Json(name = "requestId") val requestId: String? = null,
    val provider: String? = null,
    @Json(name = "reasoningEffort") val reasoningEffort: String? = null,
    @Json(name = "webSearchEnabled") val webSearchEnabled: Boolean = true,
)

@JsonClass(generateAdapter = true)
data class GenerateResponse(
    val image: String? = null,
    val images: List<GeneratedImage>? = null,
    val filename: String? = null,
    val elapsed: String? = null,
    @Json(name = "requestId") val requestId: String? = null,
    val usage: Map<String, Any>? = null,
    @Json(name = "revisedPrompt") val revisedPrompt: String? = null,
    val model: String? = null,
    val quality: String? = null,
    val size: String? = null,
    val error: String? = null,
    val code: String? = null,
)

@JsonClass(generateAdapter = true)
data class GeneratedImage(
    val image: String,
    val filename: String? = null,
    @Json(name = "revisedPrompt") val revisedPrompt: String? = null,
)

// ── Edit ──

@JsonClass(generateAdapter = true)
data class EditRequest(
    val prompt: String,
    val image: String,
    val mask: String? = null,
    val quality: String = "medium",
    val size: String = "1024x1024",
    val moderation: String = "low",
    val model: String? = null,
    val mode: String = "auto",
    @Json(name = "sessionId") val sessionId: String? = null,
    @Json(name = "requestId") val requestId: String? = null,
    @Json(name = "webSearchEnabled") val webSearchEnabled: Boolean = true,
)

@JsonClass(generateAdapter = true)
data class EditResponse(
    val image: String? = null,
    val filename: String? = null,
    val elapsed: String? = null,
    val usage: Map<String, Any>? = null,
    @Json(name = "revisedPrompt") val revisedPrompt: String? = null,
    val model: String? = null,
    val error: String? = null,
    val code: String? = null,
)

// ── History ──

@JsonClass(generateAdapter = true)
data class HistoryResponse(
    val items: List<HistoryItem>? = null,
    val sessions: List<HistorySession>? = null,
    val loose: List<HistoryItem>? = null,
    val total: Int = 0,
    @Json(name = "nextCursor") val nextCursor: HistoryCursor? = null,
)

@JsonClass(generateAdapter = true)
data class HistoryItem(
    val filename: String,
    val prompt: String? = null,
    @Json(name = "userPrompt") val userPrompt: String? = null,
    @Json(name = "revisedPrompt") val revisedPrompt: String? = null,
    val quality: String? = null,
    val size: String? = null,
    val model: String? = null,
    val kind: String? = null,
    @Json(name = "createdAt") val createdAt: Long = 0,
    @Json(name = "sessionId") val sessionId: String? = null,
    @Json(name = "isFavorite") val isFavorite: Boolean = false,
    @Json(name = "requestId") val requestId: String? = null,
)

@JsonClass(generateAdapter = true)
data class HistorySession(
    @Json(name = "sessionId") val sessionId: String,
    val title: String? = null,
    val label: String? = null,
    val items: List<HistoryItem> = emptyList(),
    @Json(name = "lastUsedAt") val lastUsedAt: Long = 0,
)

@JsonClass(generateAdapter = true)
data class HistoryCursor(
    val before: Long? = null,
    @Json(name = "beforeFilename") val beforeFilename: String? = null,
)

@JsonClass(generateAdapter = true)
data class RestoreRequest(
    @Json(name = "trashId") val trashId: String,
)

@JsonClass(generateAdapter = true)
data class FavoriteRequest(
    val filename: String,
)

@JsonClass(generateAdapter = true)
data class FavoriteResponse(
    @Json(name = "isFavorite") val isFavorite: Boolean,
)

// ── Sessions ──

@JsonClass(generateAdapter = true)
data class SessionsResponse(
    val sessions: List<SessionItem> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class SessionItem(
    val id: String,
    val title: String? = null,
    @Json(name = "createdAt") val createdAt: Long? = null,
    @Json(name = "updatedAt") val updatedAt: Long? = null,
)

@JsonClass(generateAdapter = true)
data class CreateSessionRequest(
    val title: String = "Untitled",
)

@JsonClass(generateAdapter = true)
data class CreateSessionResponse(
    val session: SessionItem,
)

@JsonClass(generateAdapter = true)
data class SessionDetailResponse(
    val session: SessionItem,
)

@JsonClass(generateAdapter = true)
data class RenameSessionRequest(
    val title: String,
)
