package com.example.tv.data.api

import com.example.tv.BuildConfig

/**
 * PUBLIC_INTERFACE
 * NetworkConfig centralizes resolution of the backend base URL.
 * It ensures a trailing slash and defaults to the provided backend URL when not configured.
 */
object NetworkConfig {
    private const val DEFAULT_BASE_URL = "https://vscode-internal-14546-beta.beta01.cloud.kavia.ai:3001/"

    // PUBLIC_INTERFACE
    fun getBaseUrl(): String {
        val configured = (BuildConfig.API_BASE_URL ?: "").trim()
        val base = if (configured.isNotEmpty()) configured else DEFAULT_BASE_URL
        return if (base.endsWith("/")) base else "$base/"
    }
}
