package com.example.tv.data.api

/**
 * PUBLIC_INTERFACE
 * HomeCategory
 * Represents logical rails on the home screen and their display titles.
 */
enum class HomeCategory(val title: String) {
    TRENDING("Trending"),
    CONTINUE_WATCHING("Continue Watching"),
    ACTION("Action"),
    DRAMA("Drama"),
    HORROR("Horror"),
    FAMILY("Family"),
    COMEDY("Comedy");
}
