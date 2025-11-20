package com.example.tv.data.api

import com.example.tv.AppConfig

/**
 * PUBLIC_INTERFACE
 * NetworkConfig centralizes resolution of the backend base URL.
 * It ensures a trailing slash and uses AppConfig as the single source of truth.
 */
object NetworkConfig {
    // PUBLIC_INTERFACE
    fun getBaseUrl(): String {
        val base = AppConfig.API_BASE_URL.trim()
        return if (base.endsWith("/")) base else "$base/"
    }
}
