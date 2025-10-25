package com.example.tv.data.api

/**
 * PUBLIC_INTERFACE
 * Domain model representing a content item displayed by the UI rails.
 * Fields:
 * - id: unique identifier used to fetch details via /api/info/{id}
 * - name: title to show
 * - poster: absolute URL to the poster image or null if not available
 */
data class ContentItem(
    val id: Int,
    val name: String,
    val poster: String?
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
