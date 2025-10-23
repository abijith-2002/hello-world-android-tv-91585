package com.example.tv.data.api

import com.squareup.moshi.Json

/**
 * PUBLIC_INTERFACE
 * Data model representing a content item returned by category endpoints.
 * Fields: name (title to show) and poster (absolute URL to the poster image).
 */
data class ContentItem(
    @Json(name = "name") val name: String,
    @Json(name = "poster") val poster: String?
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
