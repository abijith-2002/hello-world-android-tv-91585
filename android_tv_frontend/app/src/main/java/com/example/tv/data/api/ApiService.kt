package com.example.tv.data.api

import com.example.tv.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

/**
 * PUBLIC_INTERFACE
 * Retrofit API interface for category endpoints.
 * Each category is accessible at /api/{categoryPath}
 */
interface ApiService {
    /**
     * PUBLIC_INTERFACE
     * Fetch items for a given category path.
     * @param category The path segment such as "trending", "action", etc.
     * @return List of ContentItem with name and poster fields.
     */
    @GET("api/{category}")
    suspend fun getCategory(@Path("category") category: String): List<ContentItem>

    companion object {
        /**
         * PUBLIC_INTERFACE
         * Create a singleton ApiService.
         * Base URL is taken from BuildConfig.API_BASE_URL when provided and valid (must start with http/https).
         * If not provided or invalid, a safe default of "https://example.com/" is used so the app doesn't crash.
         * A trailing '/' is enforced to satisfy Retrofit's requirement.
         */
        fun create(): ApiService {
            // Sanitize and validate configured base URL
            val configured = (BuildConfig.API_BASE_URL ?: "").trim()
            val baseUrl = when {
                configured.isBlank() -> "https://example.com/"
                configured.startsWith("http://") || configured.startsWith("https://") -> {
                    if (configured.endsWith("/")) configured else "$configured/"
                }
                else -> "https://example.com/"
            }

            val client = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build()

            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}
