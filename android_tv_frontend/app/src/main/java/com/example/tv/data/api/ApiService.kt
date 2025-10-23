package com.example.tv.data.api

import com.example.tv.data.api.dto.ShowDto
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import com.example.tv.BuildConfig
import android.util.Log
import okhttp3.logging.HttpLoggingInterceptor
import com.example.tv.data.api.NetworkConfig
import java.util.concurrent.TimeUnit
import okhttp3.Interceptor
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

/**
 * PUBLIC_INTERFACE
 * Retrofit API interface for home content rails.
 *
 * Endpoints:
 * - GET /api/trending
 * - GET /api/continue_watching
 * - GET /api/action
 * - GET /api/family
 * - GET /api/comedy
 * - GET /api/horror
 * - GET /api/drama
 */
interface ApiService {

    /**
     * PUBLIC_INTERFACE
     * Fetch trending items.
     * @return List of ShowDto with name and poster fields.
     */
    @GET("api/trending")
    suspend fun getTrending(): List<ShowDto>

    /**
     * PUBLIC_INTERFACE
     * Fetch continue watching items.
     * @return List of ShowDto with name and poster fields.
     */
    @GET("api/continue_watching")
    suspend fun getContinueWatching(): List<ShowDto>

    /**
     * PUBLIC_INTERFACE
     * Fetch action items.
     * @return List of ShowDto with name and poster fields.
     */
    @GET("api/action")
    suspend fun getAction(): List<ShowDto>

    /**
     * PUBLIC_INTERFACE
     * Fetch family items.
     * @return List of ShowDto with name and poster fields.
     */
    @GET("api/family")
    suspend fun getFamily(): List<ShowDto>

    /**
     * PUBLIC_INTERFACE
     * Fetch comedy items.
     * @return List of ShowDto with name and poster fields.
     */
    @GET("api/comedy")
    suspend fun getComedy(): List<ShowDto>

    /**
     * PUBLIC_INTERFACE
     * Fetch horror items.
     * @return List of ShowDto with name and poster fields.
     */
    @GET("api/horror")
    suspend fun getHorror(): List<ShowDto>

    /**
     * PUBLIC_INTERFACE
     * Fetch drama items.
     * @return List of ShowDto with name and poster fields.
     */
    @GET("api/drama")
    suspend fun getDrama(): List<ShowDto>

    companion object {
        /**
         * PUBLIC_INTERFACE
         * Create a singleton ApiService.
         * Base URL is set to the provided backend, enforcing a trailing slash.
         */
        fun create(): ApiService {
            val baseUrl = NetworkConfig.getBaseUrl()

            // Build Host header from the base URL. If the port is the default for the scheme, omit it.
            val parsed = baseUrl.toHttpUrlOrNull()
            val hostHeader: String? = parsed?.let { url ->
                val scheme = url.scheme
                val host = url.host
                val port = url.port
                val isDefaultPort = (scheme == "http" && port == 80) || (scheme == "https" && port == 443)
                if (isDefaultPort) host else "$host:$port"
            }

            val hostHeaderInterceptor = Interceptor { chain ->
                val original = chain.request()
                val reqBuilder = original.newBuilder()
                if (!hostHeader.isNullOrBlank()) {
                    // Set Host header explicitly for every request
                    reqBuilder.header("Host", hostHeader)
                    
                }
                Log.d("HOST:::::::::::::", "$hostHeader")
                chain.proceed(reqBuilder.build())
            }

            val logging = HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.BASIC
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(hostHeaderInterceptor) // ensure Host header is set before logging
                .addInterceptor(logging)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build()

            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            Log.d("ApiService", "Using baseUrl=$baseUrl")
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}
