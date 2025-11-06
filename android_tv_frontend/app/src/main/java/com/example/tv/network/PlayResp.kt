package com.example.tv.network

/**
 * PUBLIC_INTERFACE
 * PlayResp
 *
 * Top-level data class for parsing the backend response from GET /api/play.
 * Expected JSON format: {"url": "<mediaUrl>"}
 *
 * This is defined at top level because Moshi does not support parsing into local or nested
 * classes defined inside a method scope (IllegalArgumentException). Using a top-level data class
 * ensures reflection adapters can properly construct the instance.
 */
data class PlayResp(
    /** The media URL to play. May be null or blank if backend fails to provide it. */
    val url: String?
)
