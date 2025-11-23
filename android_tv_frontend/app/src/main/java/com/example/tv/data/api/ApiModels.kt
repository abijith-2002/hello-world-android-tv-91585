package com.example.tv.data.api

/**
 * PUBLIC_INTERFACE
 * Domain model representing a content item displayed by the UI rails.
 * Fields: name (title to show) and poster (absolute URL to the poster image or null if not available).
 */
data class ContentItem(
    val name: String,
    val poster: String?
)

/**
 * PUBLIC_INTERFACE
 * Response model for /api/banner endpoint.
 * JSON shape: { "banners": ["URL1", "URL2", ...] }
 */
data class BannerResponse(
    val banners: List<String> = emptyList()
)

/**
 * PUBLIC_INTERFACE
 * Enum of supported home categories with their API path and user-facing title.
 */
enum class HomeCategory(val path: String, val title: String) {
    TRENDING("trending", "Top trending"),
    CONTINUE_WATCHING("continue_watching", "Continue watching"),
    ACTION("action", "Action"),
    DRAMA("drama", "Drama"),
    HORROR("horror", "Horror"),
    FAMILY("family", "Family"),
    COMEDY("comedy", "Comedy");
}
