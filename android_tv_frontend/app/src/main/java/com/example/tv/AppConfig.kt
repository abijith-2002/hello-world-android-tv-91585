package com.example.tv

/**
 * PUBLIC_INTERFACE
 * AppConfig
 *
 * Centralized app-level configuration constants.
 * Provides a single source of truth for the backend API base URL.
 */
object AppConfig {
    /** Base URL for the backend API. Must include scheme and trailing slash normalized by callers if needed. */
    const val API_BASE_URL: String = "https://6f4a5105.api.kavia.app"
}
