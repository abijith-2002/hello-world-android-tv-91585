package org.example.utilities

/**
 * PUBLIC_INTERFACE
 * String utilities placeholder for the utilities module.
 */
object StringUtils {
    /**
     * PUBLIC_INTERFACE
     * Joins non-null, non-blank parts with the given separator.
     */
    fun joinNonBlank(parts: List<String?>, sep: String = " "): String =
        parts.filter { !it.isNullOrBlank() }.joinToString(sep) { it!!.trim() }
}
