package com.palimson.mildy.data.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

data class HealthResponse(
    val status: String,
    val models: String
)

data class WordTimestamp(
    val word: String,
    val start: Double,
    val end: Double
)

data class SubtitleSegment(
    val id: Long,
    val start: Double,
    val end: Double,
    val text: String,
    val words: List<WordTimestamp>
)

data class TranscribeResponse(
    val language: String,
    val duration: Double,
    val segments: List<SubtitleSegment>
)

interface ApiService {
    @GET("health")
    suspend fun checkHealth(): Response<HealthResponse>

    @Multipart
    @POST("api/subtitles/transcribe")
    suspend fun transcribeAudio(
        @Part file: MultipartBody.Part,
        @Part("language") language: RequestBody
    ): Response<TranscribeResponse>

    @Multipart
    @POST("api/background/remove")
    suspend fun removeBackground(
        @Part file: MultipartBody.Part
    ): Response<ResponseBody>

    @Multipart
    @POST("api/upscale/process")
    suspend fun upscaleImage(
        @Part file: MultipartBody.Part,
        @Part("scale") scale: RequestBody
    ): Response<ResponseBody>
}
