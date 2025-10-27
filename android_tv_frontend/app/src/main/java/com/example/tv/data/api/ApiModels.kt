package com.example.tv.data.api

/**
 * PUBLIC_INTERFACE
 * Simple UI-facing content model used by Home rails.
 */
data class ContentItem(
    val name: String,
    val poster: String? = null
)

/**
 * PUBLIC_INTERFACE
 * Home categories shown as rails on the Home screen.
 * Each category exposes a 'title' for display.
 */
enum class HomeCategory(val title: String) {
    TRENDING("Trending"),
    CONTINUE_WATCHING("Continue Watching"),
    ACTION("Action"),
    DRAMA("Drama"),
    HORROR("Horror"),
    FAMILY("Family"),
    COMEDY("Comedy")
}
