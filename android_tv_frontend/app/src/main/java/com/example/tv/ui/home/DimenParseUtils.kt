package com.example.tv.ui.home

/**
 * PUBLIC_INTERFACE
 * Utility to safely parse numeric dimension strings into Ints without throwing.
 * Returns null if the value cannot be parsed.
 */
object DimenParseUtils {
    // PUBLIC_INTERFACE
    /** Parses an integer from a string or returns null if not a valid number. */
    fun parseIntOrNull(value: String?): Int? {
        if (value.isNullOrBlank()) return null
        return value.trim().toIntOrNull()
    }
}
