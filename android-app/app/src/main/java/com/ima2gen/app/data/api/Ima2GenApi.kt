package com.ima2gen.app.data.api

import com.ima2gen.app.data.api.dto.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit interface mapping all ima2-gen REST API endpoints
 * used by the Android client.
 */
interface Ima2GenApi {

    // ── Health & Status ──

    @GET("api/health")
    suspend fun getHealth(): Response<HealthResponse>

    @GET("api/providers")
    suspend fun getProviders(): Response<ProvidersResponse>

    @GET("api/billing")
    suspend fun getBilling(): Response<BillingResponse>

    @GET("api/inflight")
    suspend fun getInflight(
        @Query("kind") kind: String? = null,
        @Query("sessionId") sessionId: String? = null,
        @Query("includeTerminal") includeTerminal: String? = null,
    ): Response<InflightResponse>

    @DELETE("api/inflight/{requestId}")
    suspend fun cancelJob(@Path("requestId") requestId: String): Response<Unit>

    // ── Image Generation (Classic) ──

    @POST("api/generate")
    suspend fun generate(@Body request: GenerateRequest): Response<GenerateResponse>

    // ── Image Edit ──

    @POST("api/edit")
    suspend fun edit(@Body request: EditRequest): Response<EditResponse>

    // ── History ──

    @GET("api/history")
    suspend fun getHistory(
        @Query("limit") limit: Int? = null,
        @Query("before") before: Long? = null,
        @Query("beforeFilename") beforeFilename: String? = null,
        @Query("since") since: Long? = null,
        @Query("sessionId") sessionId: String? = null,
        @Query("groupBy") groupBy: String? = null,
        @Header("X-Ima2-Browser-Id") browserId: String? = null,
    ): Response<HistoryResponse>

    @DELETE("api/history/{filename}")
    suspend fun trashImage(@Path("filename") filename: String): Response<Map<String, Any>>

    @DELETE("api/history/{filename}/permanent")
    suspend fun deleteImagePermanent(@Path("filename") filename: String): Response<Map<String, Any>>

    @POST("api/history/{filename}/restore")
    suspend fun restoreImage(
        @Path("filename") filename: String,
        @Body body: RestoreRequest,
    ): Response<Map<String, Any>>

    @POST("api/history/favorite")
    suspend fun toggleFavorite(
        @Body body: FavoriteRequest,
        @Header("X-Ima2-Browser-Id") browserId: String,
    ): Response<FavoriteResponse>

    // ── Sessions ──

    @GET("api/sessions")
    suspend fun getSessions(): Response<SessionsResponse>

    @POST("api/sessions")
    suspend fun createSession(@Body body: CreateSessionRequest): Response<CreateSessionResponse>

    @GET("api/sessions/{id}")
    suspend fun getSession(@Path("id") id: String): Response<SessionDetailResponse>

    @PATCH("api/sessions/{id}")
    suspend fun renameSession(
        @Path("id") id: String,
        @Body body: RenameSessionRequest,
    ): Response<Map<String, Any>>

    @DELETE("api/sessions/{id}")
    suspend fun deleteSession(@Path("id") id: String): Response<Map<String, Any>>
}
